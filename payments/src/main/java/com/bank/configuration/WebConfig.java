package com.bank.configuration;


import com.bank.LoggerInterceptor;
import com.bank.utils.ClientCountry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ClientCountry clientCountry;

    public WebConfig(ClientCountry clientCountry) {
        this.clientCountry = clientCountry;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor(clientCountry)).addPathPatterns("/v1/payments");
    }
}
