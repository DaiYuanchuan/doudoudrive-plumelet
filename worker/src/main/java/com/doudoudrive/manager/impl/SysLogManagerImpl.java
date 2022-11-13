package com.doudoudrive.manager.impl;

import com.doudoudrive.config.IndexNameGenerator;
import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.manager.SysLogManager;
import com.doudoudrive.model.SysLogMessage;
import com.doudoudrive.model.TracerLogbackModel;
import com.doudoudrive.util.lang.CollectionUtil;
import com.doudoudrive.util.lang.CompressionUtil;
import com.doudoudrive.util.lang.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * <p>系统日志消息服务的通用业务处理层接口实现</p>
 * <p>2022-11-13 14:28</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("sysLogManager")
public class SysLogManagerImpl implements SysLogManager, CommandLineRunner {

    /**
     * 线程池中要保留的线程数
     */
    private static final Integer CORE_POOL_SIZE = 1;
    /**
     * 每次上传、获取的日志数量
     */
    private static final Long ELEMENTS_PER_LOG = 500L;
    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<TracerLogbackModel> SERIALIZER = new ProtostuffUtil<>();
    /**
     * 日志队列容量
     */
    private static final Integer LOGGER_QUEUE_CAPACITY = 100000;
    /**
     * 日志集中营，最多积压10万条
     */
    private static final BlockingQueue<TracerLogbackModel> TRACER_LOGBACK_QUEUE = new LinkedBlockingQueue<>(LOGGER_QUEUE_CAPACITY);
    private ElasticsearchRestTemplate restTemplate;
    private IndexNameGenerator indexNameGenerator;
    /**
     * 线程池用于异步推送系统日志
     */
    private ScheduledExecutorService executorService;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setIndexNameGenerator(IndexNameGenerator indexNameGenerator) {
        this.indexNameGenerator = indexNameGenerator;
    }

    /**
     * 批量保存系统日志消息
     *
     * @param bytes 字节流日志对象
     */
    @Override
    public void saveBatch(byte[] bytes) {
        // 解压缩对象
        byte[] decompress = CompressionUtil.decompressBytes(bytes);

        // 反序列化对象
        TracerLogbackModel tracerLogbackModel = SERIALIZER.deserialize(decompress, TracerLogbackModel.class);
        if (tracerLogbackModel == null
                || tracerLogbackModel.getLogMessageList() == null) {
            return;
        }
        TRACER_LOGBACK_QUEUE.add(tracerLogbackModel);
    }

    @Override
    public void run(String... args) {
        // 初始化线程池
        this.executorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, runnable -> {
            Thread thread = new Thread(runnable, "logback-up-thread");
            thread.setDaemon(true);
            return thread;
        });

        // 开启日志推送线程
        this.executorService.scheduleAtFixedRate(this::saveElasticsearch, NumberConstant.INTEGER_ONE, NumberConstant.INTEGER_ONE, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    /**
     * executor服务的销毁
     */
    private void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 保存日志到Elasticsearch
     */
    private void saveElasticsearch() {
        try {
            // 创建索引和放置索引映射关系
            indexNameGenerator.createIndex(SysLogMessage.class);

            // 从队列中获取日志
            TracerLogbackModel tracerLogbackModel = TRACER_LOGBACK_QUEUE.take();

            // 构建批量保存请求
            CollectionUtil.collectionCutting(tracerLogbackModel.getLogMessageList(), ELEMENTS_PER_LOG).forEach(logMessageList -> restTemplate.save(logMessageList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
