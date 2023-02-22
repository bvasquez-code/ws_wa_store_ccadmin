package com.ccadmin.app.product.model.entity;

import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table( name = "kardex" )
public class KardexEntity extends AuditTableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long kardexID;
    public String OperationCod;
    public String SourceTable;
    public String TypeOperation;
    public String ProductCod;
    public String Variant;
    public String StoreCod;
    public String WarehouseCod;
    public int NumStockBefore;
    public int NumStockMoved;
    public int NumStockAfter;

    public KardexEntity()
    {

    }
}
