package com.example.urlshortener.orchestratorservice.config;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.urlshortener.orchestratorservice.util.CorrelationIdUtil;

@Configuration
public class LoggingConfig {
    @Bean
    public OncePerRequestFilter correlationIdFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String correlationId = CorrelationIdUtil.getOrCreate();
                MDC.put("correlationId", correlationId);
                response.setHeader("X-Correlation-Id", correlationId);
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove("correlationId");
                }
            }
        };
    }
}