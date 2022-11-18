package com.doudoudrive.controller;

import com.doudoudrive.manager.SysLogManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>大日志对象，udp大于64kb时，会采用http调用</p>
 * <p>2022-11-13 01:17</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class BigTracerController {

    private SysLogManager sysLogManager;

    @Autowired
    public void setSysLogManager(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    /**
     * 接收大日志对象
     *
     * @param bytes 字节流日志对象
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/receive")
    public void receiveBigTrace(@RequestBody byte[] bytes) {
        // 接收到tcp大日志消息
        if (log.isDebugEnabled()) {
            log.debug("tcp receive big trace");
        }
        // 批量保存系统日志消息
        sysLogManager.saveBatch(bytes);
    }
}
