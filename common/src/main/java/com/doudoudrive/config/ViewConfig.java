package com.doudoudrive.config;

import com.doudoudrive.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>页面视图配置</p>
 * <p>2022-03-21 15:18</p>
 *
 * @author Dan
 **/
@Slf4j
@RestController
public class ViewConfig {

    @SneakyThrows
    @RequestMapping(value = "/404", produces = "application/json;charset=UTF-8")
    public Result<String> jumpNotFound() {
        return Result.build(404, "请求的资源不存在", null);
    }

    @SneakyThrows
    @RequestMapping(value = "/500", produces = "application/json;charset=UTF-8")
    public Result<String> jumpError() {
        return Result.build(500, "系统繁忙", null);
    }

    @SneakyThrows
    @RequestMapping(value = "/info", produces = "application/json;charset=UTF-8")
    public Result<String> jumpInfo() {
        return Result.build(200, "请求成功", null);
    }
}