package com.foryou.billingapi.api.repository;

import com.foryou.billingapi.api.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
}
