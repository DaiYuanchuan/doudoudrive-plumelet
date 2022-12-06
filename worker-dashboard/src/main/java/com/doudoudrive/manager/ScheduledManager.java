package com.doudoudrive.manager;

/**
 * <p>定时任务通用配置处理层接口</p>
 * <p>2022-12-06 16:21</p>
 *
 * @author Dan
 **/
public interface ScheduledManager {

    /**
     * 每月1号0点时创建下个月的日志索引
     */
    void createIndex();

}
