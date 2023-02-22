package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "presale_head" )
public class PresaleHeadEntity extends AuditTableEntity implements Serializable {

    @Id
    public String PresaleCod;
    public String StoreCod;
    public String ClientCod;
    public BigDecimal NumPriceSubTotal;
    public BigDecimal NumDiscount;
    public BigDecimal NumTotalPrice;
    public BigDecimal NumTotalPriceNoTax;
    public BigDecimal NumTotalTax;
    public String Commenter;
    public int PeriodId;
    public String SaleStatus;
    public String CurrencyCod;
    public String CurrencyCodSys;
    public BigDecimal NumExchangevalue;
    public String IsPaid;

}
