package com.doudoudrive.client;

import com.doudoudrive.model.CreateMqConsumerRecordRequestDTO;
import com.doudoudrive.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>日志信息服务Feign调用</p>
 * <p>2023-07-26 10:49</p>
 *
 * @author Dan
 **/
@FeignClient(name = "logServer", contextId = "logServerFeignClient")
public interface LogServerFeignClient {

    /**
     * 根据指定的topic发送延迟消息队列消息，同时创建MQ消费者记录信息
     *
     * @param createConsumerRecordRequest 创建MQ消费记录信息的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/log/consumer-record/create", produces = "application/json;charset=UTF-8")
    Result<String> createRecord(@RequestBody CreateMqConsumerRecordRequestDTO createConsumerRecordRequest);

}
