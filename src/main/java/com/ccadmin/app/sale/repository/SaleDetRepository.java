package com.ccadmin.app.sale.repository;

import com.ccadmin.app.sale.model.entity.SaleDetEntity;
import com.ccadmin.app.sale.model.entity.id.SaleDetID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleDetRepository extends JpaRepository<SaleDetEntity, SaleDetID> {
}
