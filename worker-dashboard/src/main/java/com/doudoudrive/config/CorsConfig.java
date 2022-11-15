package com.doudoudrive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * <p>全局跨域配置</p>
 * <p>2020-09-13 14:49</p>
 *
 * @author Dan
 **/
@Configuration
public class CorsConfig implements Filter {

    /**
     * 解决ajax请求跨域问题
     *
     * @return CORS 过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 添加一个请求头
        corsConfiguration.addAllowedHeader("*");
        // 设置允许跨域请求的域名
        corsConfiguration.addAllowedOrigin("*");
        // 设置允许的方法
        corsConfiguration.setAllowedMethods(Collections.singletonList("OPTIONS,HEAD,GET,PUT,POST,DELETE,PATCH"));
        // 当Credentials为true时，Origin不能为*，需要为具体的IP、域名地址 [如果接口不带Cookie,IP无需设成具体IP]
        corsConfiguration.setAllowCredentials(false);
        // 跨域允许时间
        corsConfiguration.setMaxAge(3600L);

        // 设置需要添加跨域请求的路径
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Expose-Headers'", "Authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", "OPTIONS,HEAD,GET,PUT,POST,DELETE,PATCH");
        response.setHeader("Access-Control-Allow-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " +
                "WG-App-Version, WG-Device-Id, WG-Network-Type, WG-Vendor, WG-OS-Type, WG-OS-Version, WG-Device-Model, WG-CPU, WG-Sid, WG-App-Id, WG-Token");
        String options = "OPTIONS";
        if (request.getMethod().equals(options)) {
            response.getWriter().println("ok");
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }
}
