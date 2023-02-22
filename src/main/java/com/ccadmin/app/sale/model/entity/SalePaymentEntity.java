package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.sale.model.entity.id.SalePaymentID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "sale_payments")
@IdClass( SalePaymentID.class)
public class SalePaymentEntity extends AuditTableEntity implements Serializable {
    @Id
    public int PaymentNumber;
    @Id
    public String SaleCod;
    @Id
    public String PaymentMethodCod;
    public String CurrencyCod;
    public String CurrencyCodSys;
    public BigDecimal NumExchangevalue;
    public BigDecimal NumAmountPaid;
    public BigDecimal NumAmountPaidOrigin;
    public BigDecimal NumAmountReturned;

}
