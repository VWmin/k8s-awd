package com.vwmin.k8sawd.web.conf;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/19 20:48
 */

@Configuration
public class UploadsConfig implements WebMvcConfigurer {

    /**
     * 添加静态资源映射到/uploads
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
