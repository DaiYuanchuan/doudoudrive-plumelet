package com.doudoudrive.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p></p>
 * <p>2022-11-15 00:42</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLogMessageModel {

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 追踪id
     */
    private String tracerId;

    /**
     * 调度id
     */
    private String spanId;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 日志级别 info、error
     */
    private String level;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 当前ip地址
     */
    private String currIp;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 创建的时间戳
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Shanghai")
    private Date timestamp;

}
