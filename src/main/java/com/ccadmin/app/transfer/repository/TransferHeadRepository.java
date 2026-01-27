package com.ccadmin.app.transfer.repository;

import com.ccadmin.app.shared.interfaceccadmin.CcAdminRepository;
import com.ccadmin.app.transfer.model.entity.TransferHeadEntity;
import com.ccadmin.app.transfer.model.entity.id.TransferHeadId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferHeadRepository extends JpaRepository<TransferHeadEntity, TransferHeadId>, CcAdminRepository<TransferHeadEntity, TransferHeadId> {

    @Query(value = """
            select th.* from transfer_head th
            where th.TransferCod = :transferCod
            """, nativeQuery = true)
    List<TransferHeadEntity> findByTransferCod(@Param("transferCod") String transferCod);

    @Query(value = """
            select th.* from transfer_head th
            where th.TransferCod = :transferCod
            and th.TypeOperation = :typeOperation
            """, nativeQuery = true)
    TransferHeadEntity findByTransferCodAndTypeOperation(
            @Param("transferCod") String transferCod,
            @Param("typeOperation") String typeOperation
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            select th.* from transfer_head th
            where th.TransferCod = :transferCod
            and th.TypeOperation = :typeOperation
            """, nativeQuery = true)
    TransferHeadEntity findByTransferCodAndTypeOperationForUpdate(
            @Param("transferCod") String transferCod,
            @Param("typeOperation") String typeOperation
    );
}
