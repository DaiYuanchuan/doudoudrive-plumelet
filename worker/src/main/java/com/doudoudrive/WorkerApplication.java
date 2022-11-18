package com.doudoudrive;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>worker启动器</p>
 * <p>2022-11-10 10:36</p>
 *
 * @author Dan
 **/
@Slf4j
@EnableWebMvc
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class WorkerApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

}
