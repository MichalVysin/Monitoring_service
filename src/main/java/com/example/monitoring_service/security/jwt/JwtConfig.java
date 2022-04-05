package com.example.monitoring_service.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public @Data class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private int tokenExpirationAfterDays;


    @Bean
    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }

}
