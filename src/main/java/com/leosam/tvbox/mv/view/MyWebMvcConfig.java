package com.leosam.tvbox.mv.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author admin
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 页面运行时间统计
        registry.addInterceptor(new MyTimeInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/favicon.ico");
    }
}
