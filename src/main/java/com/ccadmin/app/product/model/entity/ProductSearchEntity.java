package com.ccadmin.app.product.model.entity;

import com.ccadmin.app.product.model.entity.id.ProductSearchID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "product_search" )
@IdClass(ProductSearchID.class)
public class ProductSearchEntity extends AuditTableEntity implements Serializable {

    @Id
    public String ProductCod;
    @Id
    public String StoreCod;
    public String ProductName;
    public String ProductDesc;
    public int NumDigitalStock;
    public int NumPhysicalStock;
    public BigDecimal NumPrice;
    public int NumMaxStock;
    public int NumMinStock;
    public String IsDiscontable;
    public String DiscountType;
    public BigDecimal NumDiscountMax;
    public String BrandCod;
    public String BrandName;
    public String CategoryCod;
    public String CategoryName;
    public String CategoryDadCod;
    public String CategoryDadName;
    public String CurrencyCod;
    public String CurrencySymbol;
    public String FileCod;
    public String FileRoute;
    public int NumTrend;

    public ProductSearchEntity()
    {

    }

}
