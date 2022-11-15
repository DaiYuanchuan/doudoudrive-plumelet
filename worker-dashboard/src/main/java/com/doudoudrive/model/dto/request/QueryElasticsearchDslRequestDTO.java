package com.doudoudrive.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>查询es dsl日志信息请求实体</p>
 * <p>2022-11-15 09:45</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDslRequestDTO {

    /**
     * 请求body
     */
    private String body;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求路径
     */
    private String path;

}
