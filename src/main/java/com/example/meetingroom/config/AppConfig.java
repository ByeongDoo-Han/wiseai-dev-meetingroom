package com.example.meetingroom.config;

import com.example.meetingroom.entity.PaymentProviderType;
import com.example.meetingroom.service.PaymentGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Map<PaymentProviderType, PaymentGateway> paymentGatewayMap(List<PaymentGateway> gateways){
        return gateways.stream()
            .collect(Collectors.toMap(PaymentGateway::getProviderType,
                Function.identity()));
    }
}