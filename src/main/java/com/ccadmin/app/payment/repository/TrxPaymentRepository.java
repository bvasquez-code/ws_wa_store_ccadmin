package com.ccadmin.app.payment.repository;

import com.ccadmin.app.payment.model.entity.TrxPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxPaymentRepository extends JpaRepository<TrxPaymentEntity, Long> {


    @Query( value = """
            select * from trx_payments tp where tp.TransactionId = :TransactionId
            """,
            nativeQuery = true)
    public TrxPaymentEntity findByTransactionId(@Param("TransactionId") String TransactionId);
}
