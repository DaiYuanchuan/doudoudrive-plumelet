package com.doudoudrive.config;

import com.doudoudrive.constant.ConstantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>ES动态索引生成器</p>
 * <p>2022-11-13 15:56</p>
 *
 * @author Dan
 **/
@Component("indexNameGenerator")
public class IndexNameGenerator {

    /**
     * 生成ES索引后缀中的时间戳格式，例如：202211
     */
    private static final String PURE_DATETIME_PATTERN = "yyyyMM";
    /**
     * 时间戳格式化
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN);
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取系统日志消息动态索引
     *
     * @return 系统日志消息动态索引名称
     */
    public String sysLogbackIndex() {
        // 获取到当前月
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        // 构建es索引名称
        return ConstantConfig.IndexName.SYS_LOGBACK + ConstantConfig.SpecialSymbols.UNDERLINE + timestamp;
    }

    public boolean putTemplate(Class<?> entityClass, String templateName, String... indexPatterns) {
        // 判断索引是否存在，不存在时会自动创建
        IndexOperations indexOperations = restTemplate.indexOps(entityClass);
        PutTemplateRequest request = PutTemplateRequest.builder(templateName, indexPatterns)
                .withSettings(Document.from(indexOperations.createSettings()))
                .withMappings(Document.from(indexOperations.createMapping()))
                .build();
        return indexOperations.putTemplate(request);
    }

    /**
     * 创建索引和放置索引映射关系
     *
     * @param entityClass 实体类
     */
    public void createIndex(Class<?> entityClass) {
        // 判断索引是否存在，不存在时会自动创建
        IndexOperations indexOperations = restTemplate.indexOps(entityClass);
        if (!indexOperations.exists()) {
            synchronized (this) {
                createIndexAndPutMapping(entityClass);
            }
        }
    }

    /**
     * 创建索引和放置索引映射关系
     *
     * @param entityClass 实体类
     */
    private void createIndexAndPutMapping(Class<?> entityClass) {
        // 判断索引是否存在，不存在时会自动创建
        IndexOperations indexOperations = restTemplate.indexOps(entityClass);
        if (!indexOperations.exists()) {
            // 创建索引
            indexOperations.create();
            Document mapping = indexOperations.createMapping(entityClass);
            // 将映射写入此IndexOperations绑定到的类的索引
            indexOperations.putMapping(mapping);
        }
    }
}
