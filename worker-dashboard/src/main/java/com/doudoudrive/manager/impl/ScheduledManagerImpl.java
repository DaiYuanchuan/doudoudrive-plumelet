package com.doudoudrive.manager.impl;

import com.doudoudrive.config.IndexNameGenerator;
import com.doudoudrive.constant.ConstantConfig;
import com.doudoudrive.manager.ScheduledManager;
import com.doudoudrive.model.SysLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <p>定时任务通用配置处理层接口实现</p>
 * <p>2022-12-06 16:25</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("scheduledManager")
public class ScheduledManagerImpl implements ScheduledManager, CommandLineRunner {

    /**
     * ES动态索引生成器
     */
    private IndexNameGenerator indexNameGenerator;

    @Autowired
    public void setIndexNameGenerator(IndexNameGenerator indexNameGenerator) {
        this.indexNameGenerator = indexNameGenerator;
    }

    /**
     * 每月1号0点时创建下个月的日志索引，同时需要停用其他月份索引
     */
    @Async
    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void createIndex() {
        try {
            // 判断索引是否存在，不存在时创建索引
            indexNameGenerator.createIndex(SysLogMessage.class);
        } catch (Exception e) {
            log.error("创建索引失败:{}", e.getMessage(), e);
        }

        try {
            // 保留最近三个月的索引，其他索引需要关闭
            indexNameGenerator.closeIndex(ConstantConfig.IndexName.SYS_LOGBACK_INDEX_PATTERN, 2);
        } catch (Exception e) {
            log.error("关闭索引失败:{}", e.getMessage(), e);
        }

        try {
            // 保留最近六个月的索引，其他索引需要删除
            indexNameGenerator.deleteIndex(ConstantConfig.IndexName.SYS_LOGBACK_INDEX_PATTERN, 5);
        } catch (Exception e) {
            log.error("删除索引失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 系统启动时 创建系统logback日志索引模板
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        indexNameGenerator.putTemplate(SysLogMessage.class, ConstantConfig.IndexName.SYS_LOGBACK_TEMPLATE_NAME, ConstantConfig.IndexName.SYS_LOGBACK_INDEX_PATTERN);
    }
}
