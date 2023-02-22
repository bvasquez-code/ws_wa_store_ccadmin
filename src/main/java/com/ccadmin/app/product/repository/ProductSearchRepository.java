package com.ccadmin.app.product.repository;

import com.ccadmin.app.product.model.entity.ProductSearchEntity;
import com.ccadmin.app.product.model.entity.id.ProductSearchID;
import com.ccadmin.app.shared.interfaceccadmin.CcAdminRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductSearchRepository extends JpaRepository<ProductSearchEntity, ProductSearchID>, CcAdminRepository<ProductSearchEntity, ProductSearchID> {

    @Query(value = """
            select * from product_search ps
            where ps.ProductCod = :id or ps.ProductName like %:query% and ps.StoreCod = :storeCod and ps.Status = 'A'
            limit :init,:limit
            """, nativeQuery = true)
    @Override
    public List<ProductSearchEntity> findByQueryTextStore(
            @Param("id") String id,
            @Param("query") String query,
            @Param("storeCod") String storeCod,
            @Param("init") int init,
            @Param("limit") int limit
    );
    @Override
    @Query(value = """
            select count(1) from product_search ps
            where ps.ProductCod = :id or ps.ProductName like %:query% and ps.StoreCod = :storeCod and ps.Status = 'A'
            """, nativeQuery = true)
    public int countByQueryTextStore(
            @Param("id") String id,
            @Param("query") String query,
            @Param("storeCod") String storeCod
    );

}
