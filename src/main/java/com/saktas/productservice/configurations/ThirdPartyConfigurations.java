package com.saktas.productservice.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ThirdPartyConfigurations {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
