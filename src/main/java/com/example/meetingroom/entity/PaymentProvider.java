package com.example.meetingroom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(nullable = false)
    private PaymentProviderType providerType;

    private PaymentProvider(final String name, final String apiEndpoint, final String authInfo, final PaymentProviderType providerType) {
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.authInfo = authInfo;
        this.providerType = providerType;
    }

    public static PaymentProvider create(final String name, final String apiEndpoint, final String authInfo, final PaymentProviderType providerType) {
        return new PaymentProvider(name, apiEndpoint, authInfo, providerType);
    }
}
