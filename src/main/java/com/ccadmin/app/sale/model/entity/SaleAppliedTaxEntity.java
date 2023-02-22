package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.sale.model.entity.id.SaleAppliedTaxID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table ( name = "sale_applied_tax" )
@IdClass( SaleAppliedTaxID.class )
public class SaleAppliedTaxEntity extends AuditTableEntity implements Serializable {

    @Id
    public String TaxCod;
    @Id
    public String SaleCod;
    public BigDecimal TaxRateValue;
}
