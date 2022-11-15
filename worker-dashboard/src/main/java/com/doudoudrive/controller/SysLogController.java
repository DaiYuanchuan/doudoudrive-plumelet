package com.doudoudrive.controller;

import com.doudoudrive.manager.SysLogManager;
import com.doudoudrive.model.PageResponse;
import com.doudoudrive.model.SysLogMessageModel;
import com.doudoudrive.model.dto.request.QueryElasticsearchSysLogRequestDTO;
import com.doudoudrive.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>系统日志控制器</p>
 * <p>2022-11-14 23:22</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class SysLogController {

    private SysLogManager sysLogManager;

    @Autowired
    public void setSysLogManager(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/search", produces = "application/json;charset=UTF-8")
    public Result<PageResponse<SysLogMessageModel>> fileInfoSearch(@RequestBody @Valid QueryElasticsearchSysLogRequestDTO requestDTO,
                                                                   HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 文件信息搜索请求，获取搜索结果
        return Result.build(200, "success", sysLogManager.logSearch(requestDTO));
    }
}
