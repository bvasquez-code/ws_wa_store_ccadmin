package com.ccadmin.app.sale.model.entity;
import com.ccadmin.app.sale.exception.SaleBuildException;
import com.ccadmin.app.sale.model.entity.id.CreditNoteDetWarehouseID;
import com.ccadmin.app.shared.model.entity.AuditTableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "credit_note_det_warehouse")
@IdClass(CreditNoteDetWarehouseID.class)
public class CreditNoteDetWarehouseEntity extends AuditTableEntity implements Serializable{

    @Id
    public String CreditNoteCod;

    @Id
    public String ProductCod;

    @Id
    public String Variant;

    @Id
    public String WarehouseCod;

    public Integer NumUnit;

    public CreditNoteDetWarehouseEntity() {
    }

    public CreditNoteDetWarehouseEntity(String creditNoteCod, String productCod, String variant, String warehouseCod, Integer numUnit) {
        CreditNoteCod = creditNoteCod;
        ProductCod = productCod;
        Variant = variant;
        WarehouseCod = warehouseCod;
        NumUnit = numUnit;
    }

    /**
     * Método de validación de negocio:
     * Verifica que la cantidad de unidades no sea nula ni negativa.
     */
    public CreditNoteDetWarehouseEntity validate() {
        if (this.NumUnit == null || this.NumUnit <= 0) {
            throw new SaleBuildException("El número de unidades devueltas al almacén debe ser mayor a cero");
        }
        return this;
    }

    /**
     * Método para agregar el usuario de sesión en la auditoría.
     * Retorna la misma instancia para permitir el uso fluido (chaining).
     */
    @Override
    public CreditNoteDetWarehouseEntity session(String userCod) {
        this.addSession(userCod);
        return this;
    }
}
