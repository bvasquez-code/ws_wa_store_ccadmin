package com.ccadmin.app.sale.model.entity;

import com.ccadmin.app.sale.model.entity.id.PresaleDetWarehouseID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table( name = "presale_det_warehouse" )
@IdClass(PresaleDetWarehouseID.class)
public class PresaleDetWarehouseEntity extends AuditTableEntity implements Serializable {

    @Id
    public String PresaleCod;
    @Id
    public String ProductCod;
    @Id
    public String Variant;
    @Id
    public String WarehouseCod;
    public int NumUnit;
}
