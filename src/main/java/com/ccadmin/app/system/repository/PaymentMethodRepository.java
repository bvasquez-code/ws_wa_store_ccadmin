package com.ccadmin.app.system.repository;

import com.ccadmin.app.system.model.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity,String> {
}
