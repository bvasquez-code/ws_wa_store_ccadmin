package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_head")
public class SaleHeadEntity extends AuditTableEntity implements Serializable {

    @Id
    public String SaleCod;
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

    public SaleHeadEntity()
    {

    }

    public SaleHeadEntity(PresaleHeadEntity presaleHead)
    {
        PresaleCod = presaleHead.PresaleCod;
        StoreCod = presaleHead.StoreCod;
        ClientCod = presaleHead.ClientCod;
        NumPriceSubTotal = presaleHead.NumPriceSubTotal;
        NumDiscount = presaleHead.NumDiscount;
        NumTotalPrice = presaleHead.NumTotalPrice;
        NumTotalPriceNoTax = presaleHead.NumTotalPriceNoTax;
        NumTotalTax = presaleHead.NumTotalTax;
        Commenter = presaleHead.Commenter;
        PeriodId = presaleHead.PeriodId;
        SaleStatus = presaleHead.SaleStatus;
        CurrencyCod = presaleHead.CurrencyCod;
        CurrencyCodSys = presaleHead.CurrencyCodSys;
        NumExchangevalue = presaleHead.NumExchangevalue;
        IsPaid = presaleHead.IsPaid;
    }

    public SaleHeadEntity(String saleCod, String presaleCod, String storeCod, String clientCod, BigDecimal numPriceSubTotal, BigDecimal numDiscount, BigDecimal numTotalPrice, BigDecimal numTotalPriceNoTax, BigDecimal numTotalTax, String commenter, int periodId, String saleStatus, String currencyCod, String currencyCodSys, BigDecimal numExchangevalue, String isPaid) {
        SaleCod = saleCod;
        PresaleCod = presaleCod;
        StoreCod = storeCod;
        ClientCod = clientCod;
        NumPriceSubTotal = numPriceSubTotal;
        NumDiscount = numDiscount;
        NumTotalPrice = numTotalPrice;
        NumTotalPriceNoTax = numTotalPriceNoTax;
        NumTotalTax = numTotalTax;
        Commenter = commenter;
        PeriodId = periodId;
        SaleStatus = saleStatus;
        CurrencyCod = currencyCod;
        CurrencyCodSys = currencyCodSys;
        NumExchangevalue = numExchangevalue;
        IsPaid = isPaid;
    }
}
