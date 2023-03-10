package com.ccadmin.app.pucharse.model.entity;

import com.ccadmin.app.pucharse.model.entity.id.PucharseDetId;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "pucharse_det")
@IdClass(PucharseDetId.class)
public class PucharseDetEntity extends AuditTableEntity implements Serializable {

    @Id
    public String PucharseCod;
    @Id
    public String ProductCod;
    public String Variant;
    public int NumUnit;
    public BigDecimal NumUnitPrice;
    public BigDecimal NumTotalPrice;

    public PucharseDetEntity()
    {

    }
    public PucharseDetEntity(PucharseRequestDetEntity pucharseRequestDet)
    {
        this.ProductCod = pucharseRequestDet.ProductCod;
        this.Variant = pucharseRequestDet.Variant;
        this.NumUnit = pucharseRequestDet.NumUnit;
        this.NumUnitPrice = pucharseRequestDet.NumUnitPrice;
        this.NumTotalPrice = pucharseRequestDet.NumTotalPrice;
    }

}
