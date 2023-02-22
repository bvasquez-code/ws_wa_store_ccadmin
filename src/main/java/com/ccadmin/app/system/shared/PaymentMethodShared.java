package com.ccadmin.app.system.shared;

import com.ccadmin.app.system.model.entity.PaymentMethodEntity;
import com.ccadmin.app.system.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodShared {

    @Autowired
    private PaymentMethodService paymentMethodService;

    public PaymentMethodEntity findById(String PaymentMethodCod)
    {
        return this.paymentMethodService.findById(PaymentMethodCod);
    }
}
