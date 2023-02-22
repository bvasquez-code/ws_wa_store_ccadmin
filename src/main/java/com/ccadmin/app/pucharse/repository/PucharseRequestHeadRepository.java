package com.ccadmin.app.pucharse.repository;

import com.ccadmin.app.pucharse.model.entity.PucharseRequestHeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PucharseRequestHeadRepository extends JpaRepository<PucharseRequestHeadEntity,String> {

    @Query(value = """
            SELECT get_trx_pucharse_request_head(:storeCod) as PucharseReqCod;
            """,nativeQuery = true)
    public String getPucharseReqCod(@Param("storeCod") String storeCod);
}
