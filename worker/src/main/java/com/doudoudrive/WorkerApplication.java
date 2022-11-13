package com.doudoudrive;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>worker启动器</p>
 * <p>2022-11-10 10:36</p>
 *
 * @author Dan
 **/
@EnableAsync
@SpringBootApplication
public class WorkerApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

}
