package com.ccadmin.app.payment.service;

import com.ccadmin.app.payment.exception.TrxPaymentBuildException;
import com.ccadmin.app.payment.model.entity.TrxPaymentEntity;
import com.ccadmin.app.payment.repository.TrxPaymentRepository;
import com.ccadmin.app.shared.model.dto.ResponseWsDto;
import com.ccadmin.app.shared.service.SessionService;
import com.ccadmin.app.system.model.entity.CurrencyEntity;
import com.ccadmin.app.system.model.entity.PaymentMethodEntity;
import com.ccadmin.app.system.shared.CurrencyShared;
import com.ccadmin.app.system.shared.PaymentMethodShared;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrxPaymentService extends SessionService {

    @Autowired
    private TrxPaymentRepository trxPaymentRepository;
    @Autowired
    private PaymentMethodShared paymentMethodShared;
    @Autowired
    private CurrencyShared currencyShared;

    public TrxPaymentEntity save(TrxPaymentEntity trxPayment){

        trxPayment.addSession(getUserCod());
        trxPayment.validate();

        validatePaymentCreditNote(trxPayment);

        CurrencyEntity currencySystem = currencyShared.findCurrencySystem();
        trxPayment.CurrencyCodSys = currencySystem.CurrencyCod;

        return this.trxPaymentRepository.save(trxPayment);
    }

    public TrxPaymentEntity findById(Long TrxPaymentId){
        return this.trxPaymentRepository.findById(TrxPaymentId).get();
    }

    public List<TrxPaymentEntity> findAllById(List<Long> TrxPaymentId){
        return this.trxPaymentRepository.findAllById(TrxPaymentId);
    }

    public ResponseWsDto findDataForm(){

        ResponseWsDto rpt = new ResponseWsDto();

        List<PaymentMethodEntity> paymentMethodList = this.paymentMethodShared.findAllActive();
        List<CurrencyEntity> currencyList = this.currencyShared.findAllActive();

        rpt.AddResponseAdditional("paymentMethodList",paymentMethodList);
        rpt.AddResponseAdditional("currencyList",currencyList);

        return rpt;
    }

    private void validatePaymentCreditNote(TrxPaymentEntity trxPayment){
        if(trxPayment.PaymentMethodCod.equals("NC001")){
            TrxPaymentEntity trxPaymentDB = this.trxPaymentRepository.findByTransactionId(trxPayment.TransactionId);
            if(trxPaymentDB!=null && trxPaymentDB.Status.equals("A")){
                throw new TrxPaymentBuildException("Pago con nota de crédito ya fue usado : "+trxPaymentDB.TransactionId);
            }
        }
    }
}
