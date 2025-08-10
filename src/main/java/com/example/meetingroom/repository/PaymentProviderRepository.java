package com.example.meetingroom.repository;

import com.example.meetingroom.entity.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Long> {
}
