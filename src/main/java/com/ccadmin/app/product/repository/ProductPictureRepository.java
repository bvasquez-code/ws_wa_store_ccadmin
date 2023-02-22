package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.ProductPictureEntity;
import com.ccadmin.app.product.model.entity.id.ProductPictureID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductPictureRepository extends JpaRepository<ProductPictureEntity, ProductPictureID> {

    @Query( value = """
            select * from product_picture pp where pp.ProductCod = :ProductCod and pp.IsPrincipal = 'S' limit 1
            """, nativeQuery = true)
    public ProductPictureEntity findPrincipal(@Param("ProductCod") String ProductCod);
}
