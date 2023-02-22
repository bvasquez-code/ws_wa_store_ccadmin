package com.ccadmin.app.sale.repository;

import com.ccadmin.app.sale.model.entity.PresaleHeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PresaleHeadRepository extends JpaRepository<PresaleHeadEntity,String> {

    @Query(value = """
            SELECT get_trx_presale_head(:storeCod) as PresaleCod;
            """,nativeQuery = true)
    public String getPresaleCod(@Param("storeCod") String storeCod);
}
