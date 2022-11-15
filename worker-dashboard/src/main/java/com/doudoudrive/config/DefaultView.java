package com.doudoudrive.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * <p>默认视图配置</p>
 * <p>2022-11-15 15:32</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
public class DefaultView extends WebMvcConfigurationSupport {

    /**
     * 静态页面本地地址
     */
    @Value("${spring.thymeleaf.prefix}")
    private String pageAddress;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 默认加载index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态文件
        registry.addResourceHandler("/**")
                .addResourceLocations(pageAddress);
    }
}
