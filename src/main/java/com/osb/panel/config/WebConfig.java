package com.osb.panel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Ana sayfa yönlendirmesi — login sayfasına git
        registry.addRedirectViewController("/", "/login.xhtml");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // CV dosyalarına erişim için
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
