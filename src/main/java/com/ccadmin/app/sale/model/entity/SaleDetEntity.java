package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.product.model.entity.ProductEntity;
import com.ccadmin.app.sale.model.entity.id.SaleDetID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table( name = "sale_det")
@IdClass( SaleDetID.class )
public class SaleDetEntity extends AuditTableEntity implements Serializable {

    @Id
    public String SaleCod;
    @Id
    public String ProductCod;
    @Id
    public String Variant;
    public int NumUnit;
    public BigDecimal NumUnitPrice;
    public BigDecimal NumDiscount;
    public BigDecimal NumUnitPriceSale;
    public BigDecimal NumTotalPrice;
    public String IsAppliedTax;

    @Transient
    public List<SaleDetWarehouseEntity> DetailWarehouse;
    @Transient
    public ProductEntity product;

    public SaleDetEntity()
    {

    }

    public SaleDetEntity(PresaleDetEntity presaleDet,String SaleCod)
    {
        this.SaleCod = SaleCod;
        this.ProductCod = presaleDet.ProductCod;
        this.Variant = presaleDet.Variant;
        this.NumUnit = presaleDet.NumUnit;
        this.NumUnitPrice = presaleDet.NumUnitPrice;
        this.NumDiscount = presaleDet.NumDiscount;
        this.NumUnitPriceSale = presaleDet.NumUnitPriceSale;
        this.NumTotalPrice = presaleDet.NumTotalPrice;
        this.IsAppliedTax = "S";
    }

}
