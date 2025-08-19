package com.example.meetingroom.dto.payment;

import com.example.meetingroom.dto.payment.card.CardPaymentRequest;
import com.example.meetingroom.dto.payment.simple.SimplePaymentRequest;
import com.example.meetingroom.dto.payment.virtual.VirtualPaymentRequest;
import com.example.meetingroom.entity.PaymentProviderType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "providerType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPaymentRequest.class, name = "CARD"),
    @JsonSubTypes.Type(value = VirtualPaymentRequest.class, name = "VIRTUAL_ACCOUNT"),
    @JsonSubTypes.Type(value = SimplePaymentRequest.class, name = "SIMPLE_PAYMENT")
})
public abstract class PaymentRequest {

    private PaymentProviderType providerType;
}
