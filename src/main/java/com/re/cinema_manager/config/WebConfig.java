package com.re.cinema_manager.config;

import com.re.cinema_manager.interceptor.AdminInterceptor;
import com.re.cinema_manager.interceptor.CustomerBookingInterceptor;
import com.re.cinema_manager.interceptor.StaffInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
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
        registry.addRedirectViewController("/admin/dashboard", "/admin/reports");
    }

    /** ISO date/datetime cho input HTML5 — tránh reset ngày khi mở form sửa. */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.setDateFormatter(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        registrar.setDateTimeFormatter(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        registrar.registerFormatters(registry);
    }
}