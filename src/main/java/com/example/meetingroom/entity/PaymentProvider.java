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
    @Column(nullable = false, unique = true)
    private PaymentProviderType providerType;
}
