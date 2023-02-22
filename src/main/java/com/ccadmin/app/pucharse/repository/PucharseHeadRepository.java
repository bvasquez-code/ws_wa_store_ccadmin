package com.ccadmin.app.pucharse.repository;

import com.ccadmin.app.pucharse.model.entity.PucharseHeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PucharseHeadRepository extends JpaRepository<PucharseHeadEntity,String> {

    @Query(value = """
            SELECT get_trx_pucharse_head(:storeCod) as PucharseReqCod;
            """,nativeQuery = true)
    public String getPucharseCod(@Param("storeCod") String storeCod);
}
