package com.doudoudrive.manager;

/**
 * <p>系统日志消息服务的通用业务处理层接口</p>
 * <p>2022-11-13 14:27</p>
 *
 * @author Dan
 **/
public interface SysLogManager {

    /**
     * 批量保存系统日志消息
     *
     * @param bytes 字节流日志对象
     */
    void saveBatch(byte[] bytes);

}
