package com.doudoudrive.manager.impl;

import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.constant.RedisDelayedQueueEnum;
import com.doudoudrive.model.DelayQueueMsg;
import com.doudoudrive.util.lang.CompressionUtil;
import com.doudoudrive.util.lang.ProtostuffUtil;
import com.doudoudrive.util.lang.RedisTemplateClient;
import com.doudoudrive.util.thread.ExecutorBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.Base64;
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

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    /**
     * 线程池，用于异步拉取到期的队列元素
     */
    private static ExecutorService executor;

    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<DelayQueueMsg> SERIALIZER = new ProtostuffUtil<>();

    /**
     * Base64解码器
     */
    private static final Base64.Decoder DECODER = Base64.getDecoder();

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
                    // 原始消息体
                    byte[] body = DECODER.decode(element);
                    // 字节解压缩为字节数组
                    byte[] bytes = CompressionUtil.decompressBytes(body);
                    // 反序列化为延迟队列的消息体
                    Optional.ofNullable(SERIALIZER.deserialize(bytes, DelayQueueMsg.class))
                            // 发送到RocketMQ
                            .ifPresent(delayQueueMsg -> rocketmqTemplate.syncSend(delayedQueue.getMqTopic(), delayQueueMsg.getBody()));
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
