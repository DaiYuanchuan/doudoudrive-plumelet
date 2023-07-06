package com.doudoudrive.model;

import com.doudoudrive.constant.ConstantConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>系统日志消息实例对象</p>
 * <p>2022-11-09 21:11</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setting(shards = 2, replicas = 0, indexStoreType = ConstantConfig.StoreType.NIO_FS)
@Document(indexName = "#{@indexNameGenerator.sysLogbackIndex()}")
public class SysLogMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务标识
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String businessId;

    /**
     * 追踪id
     */
    @Field(type = FieldType.Keyword)
    private String tracerId;

    /**
     * 调度id
     */
    @Field(type = FieldType.Keyword)
    private String spanId;

    /**
     * 日志内容
     */
    @Field(type = FieldType.Wildcard)
    private String content;

    /**
     * 日志级别 info、error
     */
    @Field(type = FieldType.Keyword)
    private String level;

    /**
     * 应用名
     */
    @Field(type = FieldType.Keyword)
    private String appName;

    /**
     * 当前ip地址
     */
    @Field(type = FieldType.Keyword)
    private String currIp;

    /**
     * 类名
     */
    @Field(type = FieldType.Text)
    private String className;

    /**
     * 方法名
     */
    @Field(type = FieldType.Text)
    private String methodName;

    /**
     * 线程名
     */
    @Field(type = FieldType.Text)
    private String threadName;

    /**
     * 创建的时间戳
     */
    @Field(type = FieldType.Date)
    private Date timestamp;

}
