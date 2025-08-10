package com.example.meetingroom.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class PaymentProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String apiEndpoint;
    @Column(nullable = true)
    private String authInfo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PaymentProviderType providerType;

    @Builder
    public PaymentProvider(final Long id, final String name, final String apiEndpoint, final String authInfo, final PaymentProviderType providerType) {
        this.id = id;
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.authInfo = authInfo;
        this.providerType = providerType;
    }
}
