package com.doudoudrive.manager;

import com.doudoudrive.model.PageResponse;
import com.doudoudrive.model.SysLogMessageModel;
import com.doudoudrive.model.dto.request.QueryElasticsearchSysLogRequestDTO;

/**
 * <p>系统日志消息服务的通用业务处理层接口</p>
 * <p>2022-11-14 23:32</p>
 *
 * @author Dan
 **/
public interface SysLogManager {

    /**
     * 系统日志信息搜索
     *
     * @param request 搜索es日志信息时的请求数据模型
     * @return 系统日志消息实例对象
     */
    PageResponse<SysLogMessageModel> logSearch(QueryElasticsearchSysLogRequestDTO request);

}
