package com.vwmin.k8sawd.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/7 15:33
 */
@Configuration
public class InterceptorConf implements WebMvcConfigurer {
    private final ManagerAuthInterceptor managerAuthInterceptor;

    public InterceptorConf(ManagerAuthInterceptor managerAuthInterceptor) {
        this.managerAuthInterceptor = managerAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {



//        // 对manager下操作进行权限检查
//        registry.addInterceptor(managerAuthInterceptor)
//                .addPathPatterns("/manager/**")
//                .excludePathPatterns("/manager/login", "/manager/logout", "/manager/base", "/manager/competition/status");

    }
}
