package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KardexRepository extends JpaRepository<KardexEntity,Long> {

    @Query( value = """
            select * from kardex k
            where
            k.ProductCod = :ProductCod and k.WarehouseCod = :WarehouseCod and k.StoreCod = :StoreCod
            order by k.kardexID desc
            limit 1
            """,nativeQuery = true)
    public KardexEntity findLastMovement(
            @Param("ProductCod") String ProductCod,
            @Param("WarehouseCod") String WarehouseCod,
            @Param("StoreCod") String StoreCod
    );
}
