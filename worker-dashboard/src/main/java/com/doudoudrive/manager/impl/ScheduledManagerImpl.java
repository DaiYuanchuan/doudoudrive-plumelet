package com.doudoudrive.manager.impl;

import com.doudoudrive.config.IndexNameGenerator;
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
     * 系统logback日志索引模板的名称
     */
    private static final String SYS_LOGBACK_TEMPLATE_NAME = "sys_logback_template";
    /**
     * 系统logback日志索引模板需要匹配的索引名称，可用使用通配符
     */
    private static final String SYS_LOGBACK_INDEX_PATTERN = "sys_logback_*";
    /**
     * ES动态索引生成器
     */
    private IndexNameGenerator indexNameGenerator;

    @Autowired
    public void setIndexNameGenerator(IndexNameGenerator indexNameGenerator) {
        this.indexNameGenerator = indexNameGenerator;
    }

    /**
     * 每月1号0点时创建下个月的日志索引
     */
    @Async
    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void createIndex() {
        // 判断索引是否存在，不存在时创建索引
        indexNameGenerator.createIndex(SysLogMessage.class);
    }

    /**
     * 系统启动时 创建系统logback日志索引模板
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        indexNameGenerator.putTemplate(SysLogMessage.class, SYS_LOGBACK_TEMPLATE_NAME, SYS_LOGBACK_INDEX_PATTERN);
    }
}
