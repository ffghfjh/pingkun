package com.vip.pingkun.config;

import com.vip.pingkun.interceptor.AdminAuthInterceptor;
import com.vip.pingkun.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**").excludePathPatterns("/admin/**");
        registry.addInterceptor(adminAuthInterceptor())
                .addPathPatterns("/admin/**").excludePathPatterns("/admin/login");
    }



    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    @Bean
    public AdminAuthInterceptor adminAuthInterceptor(){return new AdminAuthInterceptor();}

    
}