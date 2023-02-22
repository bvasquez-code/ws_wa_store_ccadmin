package com.ccadmin.app.sale.repository;

import com.ccadmin.app.sale.model.entity.SaleHeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleHeadRepository extends JpaRepository<SaleHeadEntity,String> {

    @Query(value = """
            SELECT get_trx_sale_head(:storeCod) as SaleCod;
            """,nativeQuery = true)
    public String getSaleCod(@Param("storeCod") String storeCod);
}
