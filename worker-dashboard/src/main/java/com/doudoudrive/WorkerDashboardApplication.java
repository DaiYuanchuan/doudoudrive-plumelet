package com.doudoudrive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>worker前端展示</p>
 * <p>2022-11-14 23:21</p>
 *
 * @author Dan
 **/
@Slf4j
@EnableWebMvc
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class WorkerDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerDashboardApplication.class, args);
    }
}
