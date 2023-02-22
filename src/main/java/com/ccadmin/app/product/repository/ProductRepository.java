package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<ProductEntity,String> {
}
