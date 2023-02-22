package com.ccadmin.app.shared.repository;

import com.ccadmin.app.shared.model.entity.BusinessConfigEntity;
import com.ccadmin.app.shared.model.entity.id.BusinessConfigEntityID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessConfigRepository extends JpaRepository<BusinessConfigEntity, BusinessConfigEntityID> {
}
