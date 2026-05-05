package com.ccadmin.app.sale.service;


import com.ccadmin.app.payment.model.entity.TrxPaymentEntity;
import com.ccadmin.app.payment.shared.TrxPaymentShared;
import com.ccadmin.app.product.model.entity.KardexEntity;
import com.ccadmin.app.product.shared.KardexShared;
import com.ccadmin.app.sale.exception.SaleException;
import com.ccadmin.app.sale.exception.SalePaymentException;
import com.ccadmin.app.sale.model.constants.SaleConstants;
import com.ccadmin.app.sale.model.dto.CreditNoteDetailDto;
import com.ccadmin.app.sale.model.dto.CreditNoteRegisterDto;
import com.ccadmin.app.sale.model.dto.SalePaymentDto;
import com.ccadmin.app.sale.model.entity.*;
import com.ccadmin.app.sale.repository.*;
import com.ccadmin.app.shared.service.SessionService;
import com.ccadmin.app.store.model.entity.WarehouseEntity;
import com.ccadmin.app.store.shared.WarehouseShared;
import com.ccadmin.app.system.shared.CounterfoilShared;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreditNoteCreateService extends SessionService {

    @Autowired
    private CreditNoteHeadRepository creditNoteHeadRepository;
    @Autowired
    private CreditNoteDetRepository creditNoteDetRepository;
    @Autowired
    private SaleHeadRepository saleHeadRepository;
    @Autowired
    private SaleDetRepository saleDetRepository;
    @Autowired
    private SaleDocumentRepository saleDocumentRepository;
    @Autowired
    private CreditNoteDetWarehouseRepository creditNoteDetWarehouseRepository;
    @Autowired
    private CreditNoteDocumentRepository creditNoteDocumentRepository;
    @Autowired
    private CreditNoteSearchService creditNoteSearchService;
    @Autowired
    private SalePaymentSearchService salePaymentSearchService;
    @Autowired
    private SalePaymentCreateService salePaymentCreateService;
    @Autowired
    private CounterfoilShared counterfoilShared;
    @Autowired
    private WarehouseShared warehouseShared;
    @Autowired
    private KardexShared kardexShared;
    @Autowired
    private TrxPaymentShared trxPaymentShared;

    public String createCode(){
        String PresaleCod = creditNoteHeadRepository.getCreditNoteCod(getStoreCod());
        return PresaleCod;
    }

    @Transactional
    public CreditNoteDetailDto save(CreditNoteRegisterDto creditNoteRegister) throws SaleException {

        log.info("INI_CREACION_NOTA_CREDITO -->> {}",creditNoteRegister.Headboard.CreditNoteCod);

        this.validateCreditNoteRegisterDto(creditNoteRegister);

        SaleHeadEntity saleHead = this.saleHeadRepository.findById(creditNoteRegister.Headboard.SaleCod).get();

        SaleDocumentEntity saleDocument = this.saleDocumentRepository.findBySaleCod(saleHead.SaleCod);

        creditNoteRegister.DetailList.forEach( product -> {
            product.CreditNoteCod = creditNoteRegister.Headboard.CreditNoteCod;
            product.NumTotalPrice = product.NumUnitPriceSale.multiply(BigDecimal.valueOf(product.NumUnit));
            product.validate().session(getUserCod());
        });

        creditNoteRegister.Headboard.NumTotalPrice = creditNoteRegister.DetailList
                .stream()
                .map( product -> product.NumTotalPrice )
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        creditNoteRegister.Headboard
                .build(saleHead, SaleConstants.PENDING)
                .validate()
                .session(getUserCod());

        String GroupDocument = (saleDocument.DocumentCod.startsWith("B")) ? "B" : "F";


        CreditNoteDocumentEntity creditNoteDocument = (creditNoteRegister.Document == null || creditNoteRegister.Document.DocumentCod.isEmpty()) ?
                this.counterfoilShared.generateDocumentCreditNote(getStoreCod(),"07",creditNoteRegister.Headboard.CreditNoteCod,GroupDocument)
                : creditNoteRegister.Document;

        log.info("DOCUMENTO_NOTA_CREDITO -->> {}",creditNoteDocument.DocumentCod);

        this.creditNoteDetRepository.updateStatusAll(creditNoteRegister.Headboard.CreditNoteCod,"I");
        this.creditNoteHeadRepository.save(creditNoteRegister.Headboard);
        this.creditNoteDetRepository.saveAll(creditNoteRegister.DetailList);
        this.creditNoteDocumentRepository.save(creditNoteDocument);

        log.info("FIN_CREACION_NOTA_CREDITO -->> {}",creditNoteRegister.Headboard.CreditNoteCod);

        return this.creditNoteSearchService.findById(creditNoteRegister.Headboard.CreditNoteCod);
    }

    @Transactional
    public CreditNoteDetailDto confirm(CreditNoteRegisterDto creditNoteRegister) throws SaleException, SalePaymentException {

        if(creditNoteRegister.Headboard.CreditNoteStatus.equals(SaleConstants.CONFIRMED)){
            throw new SaleException("Nota de crédito ya fue confirmada");
        }

        CreditNoteHeadEntity creditNoteHead = this.creditNoteHeadRepository.findById(creditNoteRegister.Headboard.CreditNoteCod).get();
        creditNoteHead.CreditNoteStatus = SaleConstants.CONFIRMED;

        List<SalePaymentDto> salePaymentList = salePaymentSearchService.findBySaleCod(creditNoteRegister.Headboard.SaleCod);

        for(var salePaymentDto : salePaymentList){
            TrxPaymentEntity trxPayment = TrxPaymentEntity.buildReversal(salePaymentDto.TrxPayment,getUserCod());
            trxPayment = this.trxPaymentShared.save(trxPayment);
            SalePaymentEntity salePayment = SalePaymentEntity.buildReversal(salePaymentDto.SalePayment,trxPayment,getUserCod());
            salePayment = salePaymentCreateService.save(salePayment);
        }
        this.creditNoteHeadRepository.save(creditNoteHead);
        this.saleHeadRepository.updateHasCreditNote(creditNoteRegister.Headboard.SaleCod,"S");

        return this.creditNoteSearchService.findById(creditNoteRegister.Headboard.CreditNoteCod);
    }

    @Transactional
    public CreditNoteDetailDto saveReturnStock(CreditNoteRegisterDto creditNoteRegister) throws SaleException {

        CreditNoteHeadEntity creditNoteHead = this.creditNoteHeadRepository.findById(creditNoteRegister.Headboard.CreditNoteCod).get();
        WarehouseEntity warehouseDefault = this.warehouseShared.findByStore(getStoreCod()).get(0);

        creditNoteHead.IsStockReturned = "S";
        creditNoteHead.addSessionModify(getUserCod());

        List<CreditNoteDetWarehouseEntity> creditNoteDetWarehouseList = creditNoteRegister.DetailList.stream()
                .map( e -> {
                   return new CreditNoteDetWarehouseEntity(
                            e.CreditNoteCod
                           ,e.ProductCod
                           ,e.Variant
                           ,warehouseDefault.WarehouseCod
                           ,e.NumUnitStockReturned
                   ).session(getUserCod());
                }).toList();

        List<KardexEntity> KardexList = new ArrayList<>();
        for(var item : creditNoteDetWarehouseList){
            KardexEntity kardexLast = this.kardexShared.findLastMovement(item.ProductCod,item.WarehouseCod,warehouseDefault.StoreCod);
            KardexEntity kardexNoteDetWarehouse = new KardexEntity(kardexLast,item,warehouseDefault.StoreCod);
            kardexNoteDetWarehouse.addSession(getUserCod());
            KardexList.add(kardexNoteDetWarehouse);
        }

        this.creditNoteDetRepository.saveAll(creditNoteRegister.DetailList);
        this.creditNoteHeadRepository.save(creditNoteHead);
        this.creditNoteDetWarehouseRepository.saveAll(creditNoteDetWarehouseList);
        this.kardexShared.saveAll(KardexList);

        return this.creditNoteSearchService.findById(creditNoteRegister.Headboard.CreditNoteCod);
    }

    private void validateCreditNoteRegisterDto(CreditNoteRegisterDto creditNoteRegister) throws SaleException {
        if(creditNoteRegister.Headboard == null){
            throw new SaleException("No existe cabecera en la nota de crédito");
        }
        if(creditNoteRegister.DetailList == null || creditNoteRegister.DetailList.isEmpty()){
            throw new SaleException("El detalle de la nota de crédito esta vació");
        }
        if(creditNoteRegister.Headboard.CreditNoteStatus.equals(SaleConstants.CONFIRMED)){
            throw new SaleException("Nota de crédito ya fue confirmada no se puede editar");
        }
        List<SaleDetEntity> saleDetList = this.saleDetRepository.findBySaleCod(creditNoteRegister.Headboard.SaleCod);
        for(var product : creditNoteRegister.DetailList){
            if(saleDetList.stream().noneMatch(e -> e.ProductCod.equals(product.ProductCod))){
                throw new SaleException(" producto no existe en la compra de origen  "+ product.ProductCod);
            }
        }

        if(creditNoteRegister.Document == null || creditNoteRegister.Document.DocumentCod.isEmpty()){
            CreditNoteHeadEntity creditNoteHead = this.creditNoteHeadRepository.findBySaleCod(creditNoteRegister.Headboard.SaleCod);
            if(creditNoteHead != null){
                throw new SaleException("Venta ya tiene asociada una nota de crédito");
            }
        }
    }
}
