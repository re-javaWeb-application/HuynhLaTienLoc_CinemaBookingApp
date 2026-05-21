package com.re.cinema_manager.config;

import com.re.cinema_manager.interceptor.AdminInterceptor;
import com.re.cinema_manager.interceptor.CustomerBookingInterceptor;
import com.re.cinema_manager.interceptor.StaffInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;
    private final StaffInterceptor staffInterceptor;
    private final CustomerBookingInterceptor customerBookingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");

        registry.addInterceptor(staffInterceptor)
                .addPathPatterns("/staff/**");

        registry.addInterceptor(customerBookingInterceptor)
                .addPathPatterns("/booking/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/admin/movie", "/admin/movies");
        registry.addRedirectViewController("/admin/movie/", "/admin/movies");
    }
}