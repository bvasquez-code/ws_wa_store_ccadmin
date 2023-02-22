package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity,String> {
}
