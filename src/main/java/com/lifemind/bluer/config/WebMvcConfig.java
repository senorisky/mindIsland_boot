package com.lifemind.bluer.config;

/**
 * @Classname WebMvcConfig
 * @Description TODO
 * @Date 2022/11/17 9:13
 * @Created by senorisky
 */


import com.lifemind.bluer.interceptor.BasicInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file-save-path}")
    private String fileSavePath;

    //图片请求路径映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/LifeMind/**").addResourceLocations("file:" + fileSavePath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                // 那些路径不拦截
                .excludePathPatterns("/user/login", "/user/regist", "/error");
    }

    @Bean
    public BasicInterceptor loginInterceptor() {
        return new BasicInterceptor();
    }
}
