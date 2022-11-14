package com.doudoudrive.manager.impl;

import com.doudoudrive.config.IndexNameGenerator;
import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.manager.SysLogManager;
import com.doudoudrive.model.SysLogMessage;
import com.doudoudrive.model.TracerLogbackModel;
import com.doudoudrive.util.lang.CollectionUtil;
import com.doudoudrive.util.lang.CompressionUtil;
import com.doudoudrive.util.lang.ProtostuffUtil;
import com.doudoudrive.util.thread.ExecutorBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>系统日志消息服务的通用业务处理层接口实现</p>
 * <p>2022-11-13 14:28</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("sysLogManager")
public class SysLogManagerImpl implements SysLogManager, CommandLineRunner, Closeable {

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

    /**
     * 线程池，用于异步推送系统日志
     */
    private static ExecutorService executor;
    /**
     * es操作模板
     */
    private ElasticsearchRestTemplate restTemplate;
    /**
     * ES动态索引生成器
     */
    private IndexNameGenerator indexNameGenerator;

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
        this.shutdown(Boolean.TRUE);

        // 初始化线程池
        executor = ExecutorBuilder.create()
                .setCorePoolSize(NumberConstant.INTEGER_FIVE)
                .setMaxPoolSize(NumberConstant.INTEGER_FIVE)
                .setAllowCoreThreadTimeOut(true)
                .setWorkQueue(new LinkedBlockingQueue<>(NumberConstant.INTEGER_ONE))
                // 设置线程拒绝策略，丢弃队列中最旧的
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();
        executor.submit(this::saveElasticsearch);
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
     * 保存日志到Elasticsearch
     */
    private void saveElasticsearch() {
        for (; ; ) {
            try {
                // 从队列中获取日志
                TracerLogbackModel tracerLogbackModel = TRACER_LOGBACK_QUEUE.take();

                // 创建索引和放置索引映射关系
                indexNameGenerator.createIndex(SysLogMessage.class);

                // 构建批量保存请求
                CollectionUtil.collectionCutting(tracerLogbackModel.getLogMessageList(), ELEMENTS_PER_LOG).forEach(logMessageList -> restTemplate.save(logMessageList));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
