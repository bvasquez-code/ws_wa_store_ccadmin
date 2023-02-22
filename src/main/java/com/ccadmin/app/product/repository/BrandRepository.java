package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<BrandEntity,String> {
}
