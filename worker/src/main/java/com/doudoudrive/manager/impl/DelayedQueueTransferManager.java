package com.doudoudrive.manager.impl;

import com.alibaba.fastjson.JSON;
import com.doudoudrive.client.LogServerFeignClient;
import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.constant.RedisDelayedQueueEnum;
import com.doudoudrive.model.CreateMqConsumerRecordRequestDTO;
import com.doudoudrive.util.http.Result;
import com.doudoudrive.util.lang.RedisTemplateClient;
import com.doudoudrive.util.thread.ExecutorBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>延迟队列转移服务通用处理层</p>
 * <p>2023-05-28 21:25</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("delayedQueueTransferManager")
public class DelayedQueueTransferManager implements CommandLineRunner, Closeable {

    private RedisTemplateClient redisTemplateClient;
    private LogServerFeignClient logServerFeignClient;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    @Autowired
    public void setLogServerFeignClient(LogServerFeignClient logServerFeignClient) {
        this.logServerFeignClient = logServerFeignClient;
    }

    /**
     * 线程池，用于异步拉取到期的队列元素
     */
    private static ExecutorService executor;

    @Override
    public void run(String... args) {
        this.shutdown(Boolean.TRUE);
        // 需要监听的延迟队列枚举
        RedisDelayedQueueEnum[] delayedQueueEnums = RedisDelayedQueueEnum.values();

        // 初始化线程池
        executor = ExecutorBuilder.create()
                .setCorePoolSize(Math.min(delayedQueueEnums.length, Runtime.getRuntime().availableProcessors() * NumberConstant.INTEGER_TWO))
                .setMaxPoolSize(Math.min(delayedQueueEnums.length, Runtime.getRuntime().availableProcessors() * NumberConstant.INTEGER_TWO))
                .setAllowCoreThreadTimeOut(true)
                .setWorkQueue(new LinkedBlockingQueue<>(NumberConstant.INTEGER_ONE_THOUSAND))
                // 设置线程拒绝策略，丢弃队列中最旧的
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();

        for (RedisDelayedQueueEnum delayedQueueEnum : RedisDelayedQueueEnum.values()) {
            executor.submit(() -> this.pullDelayMessage(delayedQueueEnum));
        }
    }

    @Override
    public void close() {
        this.shutdown(Boolean.FALSE);
    }

    /**
     * executor服务的销毁
     *
     * @param now 是否立即销毁
     */
    private void shutdown(boolean now) {
        if (executor != null) {
            if (now) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }
        }
    }

    /**
     * 拉取延迟队列中的到期元素
     *
     * @param delayedQueue 延迟队列枚举
     */
    private void pullDelayMessage(RedisDelayedQueueEnum delayedQueue) {
        while (null != executor && !executor.isShutdown()) {
            try {
                // 从延迟队列中拉取到期元素
                Optional.ofNullable((String) redisTemplateClient.leftPop(delayedQueue.getTopic(), NumberConstant.INTEGER_ZERO, TimeUnit.SECONDS)).ifPresent(element -> {
                    CreateMqConsumerRecordRequestDTO consumerRecordRequest = CreateMqConsumerRecordRequestDTO.builder()
                            .delayedQueue(delayedQueue.name())
                            .element(element)
                            .build();
                    try {
                        // 发送MQ消息，同时创建消费记录信息
                        Result<String> result = logServerFeignClient.createRecord(consumerRecordRequest);
                        if (Result.isNotSuccess(result)) {
                            log.error("createConsumerRecord error：{}", JSON.toJSONString(result));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
