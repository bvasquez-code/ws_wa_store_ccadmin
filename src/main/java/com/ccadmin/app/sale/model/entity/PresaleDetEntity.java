package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.product.model.entity.ProductEntity;
import com.ccadmin.app.sale.model.entity.id.PresaleDetID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table( name = "presale_det" )
@IdClass(PresaleDetID.class)
public class PresaleDetEntity extends AuditTableEntity implements Serializable {

    @Id
    public String PresaleCod;
    @Id
    public String ProductCod;
    @Id
    public String Variant;
    public int NumUnit;
    public BigDecimal NumUnitPrice;
    public BigDecimal NumDiscount;
    public BigDecimal NumUnitPriceSale;
    public BigDecimal NumTotalPrice;

    @Transient
    public List<PresaleDetWarehouseEntity> DetailWarehouse;

    @Transient
    public ProductEntity product;
}
