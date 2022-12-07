package com.doudoudrive.model.dto.request;

import com.doudoudrive.constant.NumberConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>查询系统日志信息请求实体</p>
 * <p>2022-11-14 23:29</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchSysLogRequestDTO {

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date endTime;

    /**
     * 关键字
     */
    private List<String> keyword;

    /**
     * 页码, 第几页
     */
    private Integer page;

    /**
     * 每页大小、记录数
     */
    private Integer pageSize;

    /**
     * 当前页排序
     */
    private Boolean sort;

    /**
     * 第几页
     *
     * @return 返回页码，默认为1，最小为1
     */
    public Integer getPage() {
        return Math.max(Optional.ofNullable(page).orElse(NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_ONE);
    }

    /**
     * 每页记录数
     *
     * @return 返回每页的大小，默认为20，最小为1，最大100
     */
    public Integer getPageSize() {
        return Math.min(Math.max(Optional.ofNullable(pageSize).orElse(NumberConstant.INTEGER_TWENTY), NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_HUNDRED);
    }

    /**
     * 当前页排序，只适用于当前页，不适用于全局排序
     *
     * @return 返回排序，默认为false，false为降序，true为升序
     */
    public Boolean getSort() {
        return Optional.ofNullable(sort).orElse(Boolean.FALSE);
    }
}
