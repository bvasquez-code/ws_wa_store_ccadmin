package com.ccadmin.app.sale.repository;

import com.ccadmin.app.sale.model.entity.SaleDocumentEntity;
import com.ccadmin.app.sale.model.entity.id.SaleDocumentID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleDocumentRepository extends JpaRepository<SaleDocumentEntity, SaleDocumentID> {
}
