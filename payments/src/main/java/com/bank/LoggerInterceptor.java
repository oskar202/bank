package com.bank;


import com.bank.utils.ClientCountry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoggerInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

    private final ClientCountry clientCountry;

    public LoggerInterceptor(ClientCountry clientCountry) {
        this.clientCountry = clientCountry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[{}]{} From: {}", request.getMethod(), request.getRequestURI(), clientCountry.getUserLocationByIp());
        return true;
    }
}
