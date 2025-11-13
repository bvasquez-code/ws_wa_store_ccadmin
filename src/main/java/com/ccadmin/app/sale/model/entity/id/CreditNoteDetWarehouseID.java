package com.ccadmin.app.sale.model.entity.id;

import jakarta.persistence.Id;

import java.io.Serializable;

public class CreditNoteDetWarehouseID implements Serializable {

    public String CreditNoteCod;
    public String ProductCod;
    public String Variant;
    public String WarehouseCod;

    public CreditNoteDetWarehouseID(){

    }
    public CreditNoteDetWarehouseID(String creditNoteCod, String productCod, String variant, String warehouseCod) {
        CreditNoteCod = creditNoteCod;
        ProductCod = productCod;
        Variant = variant;
        WarehouseCod = warehouseCod;
    }
}
