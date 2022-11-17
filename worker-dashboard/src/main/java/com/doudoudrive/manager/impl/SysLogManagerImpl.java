package com.doudoudrive.manager.impl;

import com.doudoudrive.constant.ConstantConfig;
import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.manager.SysLogManager;
import com.doudoudrive.model.PageResponse;
import com.doudoudrive.model.SysLogMessage;
import com.doudoudrive.model.SysLogMessageModel;
import com.doudoudrive.model.dto.request.QueryElasticsearchSysLogRequestDTO;
import com.doudoudrive.util.lang.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>系统日志消息服务的通用业务处理层接口实现</p>
 * <p>2022-11-14 23:32</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("sysLogManager")
public class SysLogManagerImpl implements SysLogManager {

    private static final String CONTENT = "content";
    /**
     * 模糊搜索时的通配符
     */
    private static final String FUZZY_SEARCH = "*%s*";
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * must(QueryBuilders) : AND
     * mustNot(QueryBuilders): NOT
     * should(QueryBuilders):OR
     *
     * @param request 搜索es日志信息时的请求数据模型
     * @return 分页查询的统一出参
     */
    @Override
    public PageResponse<SysLogMessageModel> logSearch(QueryElasticsearchSysLogRequestDTO request) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (CollectionUtil.isNotEmpty(request.getKeyword())) {
            for (String keyword : request.getKeyword()) {
                // 关键字按照冒号分割，冒号前面是字段名，冒号后面是字段值
                String[] key = keyword.split(ConstantConfig.SpecialSymbols.ENGLISH_COLON);

                if (StringUtils.isNotBlank(key[0]) && StringUtils.isNotBlank(key[1])) {
                    if (key[0].trim().equals(CONTENT)) {
                        // 日志内容需要使用模糊搜索
                        builder.must(QueryBuilders.wildcardQuery(CONTENT, String.format(FUZZY_SEARCH, key[1].trim())));
                    } else {
                        builder.must(QueryBuilders.termQuery(key[0].trim(), key[1].trim()));
                    }
                }
            }
        }

        if (request.getStartTime() != null && request.getEndTime() != null) {
            builder.must(QueryBuilders.rangeQuery("timestamp").gte(request.getStartTime()).lte(request.getEndTime()));
        }

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 排序字段构建
        queryBuilder.withSorts(SortBuilders.fieldSort(ConstantConfig.DiskFileSearchOrderBy.BUSINESS_ID.fieldName).order(SortOrder.DESC));

        // 构建分页语句
        queryBuilder.withPageable(PageRequest.of(request.getPage() - NumberConstant.INTEGER_ONE, request.getPageSize()));

        NativeSearchQuery searchQuery = queryBuilder.build();
        // 返回实际命中数
        searchQuery.setTrackTotalHits(true);

        // 执行搜素请求
        SearchHits<SysLogMessage> searchHits = restTemplate.search(searchQuery, SysLogMessage.class);

        // 构建返回对象
        PageResponse<SysLogMessageModel> response = new PageResponse<>();
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setRows(convertResponse(searchHits.getSearchHits()));
        response.setTotal(searchHits.getTotalHits());
        return response;
    }

    public List<SysLogMessageModel> convertResponse(List<SearchHit<SysLogMessage>> searchHit) {
        if (searchHit == null) {
            return null;
        }
        List<SysLogMessageModel> list = new ArrayList<>(searchHit.size());
        for (SearchHit<SysLogMessage> searchHit1 : searchHit) {
            SysLogMessage source = searchHit1.getContent();
            list.add(SysLogMessageModel.builder()
                    .businessId(source.getBusinessId())
                    .tracerId(source.getTracerId())
                    .spanId(source.getSpanId())
                    .content(source.getContent())
                    .level(source.getLevel())
                    .appName(source.getAppName())
                    .currIp(source.getCurrIp())
                    .className(source.getClassName())
                    .methodName(source.getMethodName())
                    .threadName(source.getThreadName())
                    .timestamp(source.getTimestamp())
                    .build());
        }
        return list;
    }
}
