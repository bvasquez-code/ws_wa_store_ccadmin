package com.ccadmin.app.product.model.entity;

import com.ccadmin.app.product.model.entity.id.ProductPictureID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "product_picture")
@IdClass(ProductPictureID.class)
public class ProductPictureEntity extends AuditTableEntity implements Serializable {

    @Id
    public String ProductCod;
    @Id
    public String FileCod;
    public String IsPrincipal;

}
