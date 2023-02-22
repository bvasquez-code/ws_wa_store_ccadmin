package com.ccadmin.app.product.model.entity;

import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table( name = "product")
public class ProductEntity  extends AuditTableEntity implements Serializable  {

    @Id
    public String ProductCod;
    public String CategoryCod;
    public String BrandCod;
    public String ProductName;
    public String ProductDesc;

}
