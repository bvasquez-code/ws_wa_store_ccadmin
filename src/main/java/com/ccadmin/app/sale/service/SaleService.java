package com.ccadmin.app.sale.service;

import com.ccadmin.app.product.model.entity.KardexEntity;
import com.ccadmin.app.product.shared.KardexShared;
import com.ccadmin.app.sale.model.dto.PresaleDetailDto;
import com.ccadmin.app.sale.model.dto.SaleDetailDto;
import com.ccadmin.app.sale.model.dto.SalePaymentDto;
import com.ccadmin.app.sale.model.dto.SaleRegisterDto;
import com.ccadmin.app.sale.model.entity.*;
import com.ccadmin.app.sale.repository.*;
import com.ccadmin.app.shared.service.SessionService;
import com.ccadmin.app.system.model.entity.CurrencyEntity;
import com.ccadmin.app.system.shared.CounterfoilShared;
import com.ccadmin.app.system.shared.CurrencyShared;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService extends SessionService {

    @Autowired
    private SaleHeadRepository saleHeadRepository;
    @Autowired
    private SaleDetRepository saleDetRepository;
    @Autowired
    private SaleDetWarehouseRepository saleDetWarehouseRepository;
    @Autowired
    private SaleAppliedTaxRepository saleAppliedTaxRepository;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private SalePaymentRepository salePaymentRepository;
    @Autowired
    private SaleDocumentRepository saleDocumentRepository;
    @Autowired
    private CurrencyShared currencyShared;
    @Autowired
    private KardexShared kardexShared;
    @Autowired
    private CounterfoilShared counterfoilShared;

    @Transactional
    public SaleDetailDto save(PresaleDetailDto presaleDetail)
    {
        SaleDetailDto saleDetail = new SaleDetailDto();
        SaleRegisterDto saleRegister = new SaleRegisterDto();

        PeriodEntity period = this.periodRepository.findPeriodActuality();
        List<TaxEntity> taxList = this.taxRepository.findAllActive();

        SaleHeadEntity saleHead = new SaleHeadEntity(presaleDetail.Headboard);
        saleHead.SaleCod = this.saleHeadRepository.getSaleCod(getStoreCod());
        saleHead.PeriodId = period.PeriodId;
        saleHead.NumTotalTax = calculateBaseTax(taxList,saleHead.NumTotalPrice);
        saleHead.NumTotalPriceNoTax = new BigDecimal( saleHead.NumTotalPrice.doubleValue() -  saleHead.NumTotalTax.doubleValue());
        saleHead.addSession(getUserCod(),true);

        List<SaleDetEntity> detailSale = new ArrayList<>();
        List<SaleDetWarehouseEntity> detailSaleWarehouse = new ArrayList<>();
        List<SaleAppliedTaxEntity> SaleAppliedTaxList = new ArrayList<>();

        for( var item : presaleDetail.DetailList )
        {
            SaleDetEntity saleDet = new SaleDetEntity(item,saleHead.SaleCod);
            saleDet.addSession(getUserCod(),true);
            detailSale.add(saleDet);

            if( item.DetailWarehouse != null && item.DetailWarehouse.size() >0 )
            {
                for (var itemWarehouse : item.DetailWarehouse)
                {
                    SaleDetWarehouseEntity saleDetWarehouse = new SaleDetWarehouseEntity(itemWarehouse,saleHead.SaleCod);
                    saleDetWarehouse.addSession(getUserCod(),true);
                    detailSaleWarehouse.add(saleDetWarehouse);
                }
            }
        }

        for(var item : taxList)
        {
            SaleAppliedTaxEntity saleAppliedTax = new SaleAppliedTaxEntity();
            saleAppliedTax.SaleCod = saleHead.SaleCod;
            saleAppliedTax.TaxCod = item.TaxCod;
            saleAppliedTax.TaxRateValue = item.TaxRateValue;
            saleAppliedTax.addSession(getUserCod(),true);
            SaleAppliedTaxList.add(saleAppliedTax);
        }

        this.saleHeadRepository.save(saleHead);
        this.saleDetRepository.saveAll(detailSale);
        this.saleDetWarehouseRepository.saveAll(detailSaleWarehouse);
        this.saleAppliedTaxRepository.saveAll(SaleAppliedTaxList);

        return saleDetail;
    }

    private BigDecimal calculateBaseTax(List<TaxEntity> taxList,BigDecimal total)
    {
        BigDecimal taxTotal = new BigDecimal(
                taxList.stream().mapToDouble( e -> e.TaxRateValue.doubleValue() ).sum()
        );

        return new BigDecimal(  total.doubleValue() / ( (taxTotal.doubleValue() + 100)/100)  );
    }

    @Transactional
    public SalePaymentEntity confirm(String SaleCod,String DocumentType,String CounterfoilCod)
    {
        SaleHeadEntity saleHead = this.saleHeadRepository.findById(SaleCod).get();
        List<KardexEntity> kardexList = new ArrayList<>();

        List<SaleDetWarehouseEntity> saleDetWarehouseList = this.saleDetWarehouseRepository.findBySaleCod(SaleCod);

        for(var item : saleDetWarehouseList)
        {
            KardexEntity kardexBefore = this.kardexShared.findLastMovement(item.ProductCod,item.WarehouseCod,saleHead.StoreCod);

            KardexEntity kardex = new KardexEntity();
            kardex.OperationCod = SaleCod;
            kardex.SourceTable = "sale_head";
            kardex.TypeOperation = "R";
            kardex.ProductCod = item.ProductCod;
            kardex.Variant = item.Variant;
            kardex.StoreCod = saleHead.StoreCod;
            kardex.WarehouseCod = item.WarehouseCod;
            kardex.NumStockBefore = kardexBefore.NumStockAfter;
            kardex.NumStockMoved = item.NumUnit;
            kardex.NumStockAfter = kardexBefore.NumStockAfter - item.NumUnit;
            kardex.addSession(getUserCod(),true);
            kardexList.add(kardex);
        }

        saleHead.SaleStatus = "C";
        saleHead.addSession(getUserCod(),false);

        SaleDocumentEntity saleDocument = counterfoilShared.generateDocumentSale(saleHead.StoreCod,DocumentType);
        saleDocument.SaleCod = saleHead.SaleCod;
        saleDocument.addSession(getUserCod(),true);

        this.saleHeadRepository.save(saleHead);
        this.kardexShared.saveAll(kardexList);
        this.saleDocumentRepository.save(saleDocument);

        return new SalePaymentEntity();
    }

    @Transactional
    public SalePaymentEntity addPayment(SalePaymentDto payment) throws Exception {

        SalePaymentEntity salePayment = new SalePaymentEntity();
        CurrencyEntity currency = this.currencyShared.findById(payment.CurrencyCod);
        CurrencyEntity currencySys = this.currencyShared.findCurrencySystem();

        BigDecimal TotalPayment = this.salePaymentRepository.findTotalPayment(payment.SaleCod);
        SaleHeadEntity saleHead = this.saleHeadRepository.findById(payment.SaleCod).get();

        if( TotalPayment.doubleValue() >= saleHead.NumTotalPrice.doubleValue() )
        {
            throw new Exception("Sale is completed payment");
        }

        salePayment.NumExchangevalue = new BigDecimal(1);

        if( !saleHead.CurrencyCod.equals(currency.CurrencyCod) )
        {
            salePayment.NumExchangevalue = currency.NumExchangevalue;
        }
        salePayment.PaymentNumber = 1 + this.salePaymentRepository.countTotalPayment(payment.SaleCod);
        salePayment.NumAmountPaidOrigin = payment.NumAmountPaid;
        salePayment.CurrencyCod = payment.CurrencyCod;
        salePayment.NumAmountPaid = new BigDecimal(salePayment.NumAmountPaidOrigin.doubleValue() * salePayment.NumExchangevalue.doubleValue() );
        salePayment.PaymentMethodCod = payment.PaymentMethodCod;
        salePayment.SaleCod = payment.SaleCod;
        salePayment.CurrencyCodSys = saleHead.CurrencyCodSys;
        salePayment.NumAmountReturned = new BigDecimal(0);

        if( TotalPayment.doubleValue() + salePayment.NumAmountPaid.doubleValue() >= saleHead.NumTotalPrice.doubleValue() )
        {
            salePayment.NumAmountReturned = new BigDecimal(
                    TotalPayment.doubleValue() + salePayment.NumAmountPaid.doubleValue() - saleHead.NumTotalPrice.doubleValue()
            );

            confirm(payment.SaleCod,payment.DocumentType,"");

        }
        salePayment.addSession(getUserCod(),true);
        salePaymentRepository.save(salePayment);

        return salePayment;
    }
}
