package com.foryou.billingapi.api.repository;

import com.foryou.billingapi.api.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {

    @Query("select p " +
            "from Payments p " +
            "where p.memberId=:memberId " +
            "and p.delYN=false")
    List<Payments> usePaymentList(String memberId);
}
