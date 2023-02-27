package com.ccadmin.app.sale.service;

import com.ccadmin.app.product.shared.ProductShared;
import com.ccadmin.app.sale.model.dto.PresaleDetailDto;
import com.ccadmin.app.sale.model.dto.PresaleRegisterDto;
import com.ccadmin.app.sale.model.dto.SaleDetailDto;
import com.ccadmin.app.sale.model.entity.PeriodEntity;
import com.ccadmin.app.sale.model.entity.PresaleDetWarehouseEntity;
import com.ccadmin.app.sale.model.entity.PresaleHeadEntity;
import com.ccadmin.app.sale.model.entity.id.PresaleDetWarehouseID;
import com.ccadmin.app.sale.repository.PeriodRepository;
import com.ccadmin.app.sale.repository.PresaleDetRepository;
import com.ccadmin.app.sale.repository.PresaleDetWarehouseRepository;
import com.ccadmin.app.sale.repository.PresaleHeadRepository;
import com.ccadmin.app.shared.model.dto.ResponsePageSearch;
import com.ccadmin.app.shared.model.dto.ResponseWsDto;
import com.ccadmin.app.shared.model.dto.SearchDto;
import com.ccadmin.app.shared.service.SearchService;
import com.ccadmin.app.shared.service.SessionService;
import com.ccadmin.app.store.model.entity.WarehouseEntity;
import com.ccadmin.app.store.shared.WarehouseShared;
import com.ccadmin.app.system.model.entity.CurrencyEntity;
import com.ccadmin.app.system.shared.CurrencyShared;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PresaleService extends SessionService {

    @Autowired
    private PresaleHeadRepository presaleHeadRepository;
    @Autowired
    private PresaleDetRepository presaleDetRepository;
    @Autowired
    private PresaleDetWarehouseRepository presaleDetWarehouseRepository;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private CurrencyShared currencyShared;
    @Autowired
    private WarehouseShared warehouseShared;
    @Autowired
    private ProductShared productShared;
    @Autowired
    private SaleService saleService;
    private SearchService searchService;

    @Transactional
    public PresaleDetailDto save(PresaleRegisterDto presaleRegister) {
        List<PresaleDetWarehouseEntity> presaleDetWarehouseList = new ArrayList<>();
        boolean isNewPresale = ( presaleRegister.Headboard.PresaleCod == null ||  presaleRegister.Headboard.PresaleCod.trim().isEmpty() == true);
        boolean IsMultipleWarehouse = warehouseShared.IsMultipleWarehouse(presaleRegister.Headboard.StoreCod);

        if( isNewPresale  )
        {
            presaleRegister.Headboard.PresaleCod = presaleHeadRepository.getPresaleCod(getStoreCod());
        }
        else
        {
            this.presaleDetRepository.updateStatusAll(presaleRegister.Headboard.PresaleCod,"I");
            this.presaleDetWarehouseRepository.updateStatusAll(presaleRegister.Headboard.PresaleCod,"I");
        }

        CurrencyEntity currencySystem = this.currencyShared.findCurrencySystem();
        PeriodEntity period = this.periodRepository.findPeriodActuality();

        presaleRegister.Headboard.addSession(getUserCod(),isNewPresale);
        presaleRegister.Headboard.StoreCod = getStoreCod();
        presaleRegister.Headboard.CurrencyCodSys = currencySystem.CurrencyCod;
        presaleRegister.Headboard.NumExchangevalue = new BigDecimal(1);
        presaleRegister.Headboard.PeriodId = period.PeriodId;

        if(!presaleRegister.Headboard.CurrencyCodSys.equals(presaleRegister.Headboard.CurrencyCod))
        {
            CurrencyEntity currencyPucharse = this.currencyShared.findById(presaleRegister.Headboard.CurrencyCod);
            presaleRegister.Headboard.NumExchangevalue = currencyPucharse.NumExchangevalue;
        }

        for(var product : presaleRegister.DetailList)
        {
            product.addSession(getUserCod(),isNewPresale);
            product.PresaleCod = presaleRegister.Headboard.PresaleCod;
            product.NumUnitPriceSale = product.NumUnitPrice.subtract( product.NumDiscount );
            product.NumTotalPrice = product.NumUnitPriceSale.multiply(new BigDecimal(product.NumUnit));
        }

        if(!IsMultipleWarehouse)
        {
            presaleDetWarehouseList = generateDetWarehouseDefault(presaleRegister,isNewPresale);
        }

        presaleRegister.Headboard.NumPriceSubTotal = new BigDecimal(
                presaleRegister.DetailList.stream().mapToDouble(
                        e-> e.NumUnitPrice.doubleValue() * e.NumUnit
                ).sum()
        );

        presaleRegister.Headboard.NumDiscount = new BigDecimal(
                presaleRegister.DetailList.stream().mapToDouble(
                        e-> e.NumDiscount.doubleValue() * e.NumUnit
                ).sum()
        );

        presaleRegister.Headboard.NumTotalPrice = presaleRegister.Headboard.NumPriceSubTotal
                .subtract( presaleRegister.Headboard.NumDiscount );

        presaleRegister.Headboard.NumTotalTax = new BigDecimal(0);
        presaleRegister.Headboard.NumTotalPriceNoTax = new BigDecimal(0);

        if( presaleRegister.Headboard.ClientCod != null && presaleRegister.Headboard.ClientCod.isEmpty() )
        {
            presaleRegister.Headboard.ClientCod = null;
        }

        this.presaleHeadRepository.save(presaleRegister.Headboard);
        this.presaleDetRepository.saveAll(presaleRegister.DetailList);
        this.presaleDetWarehouseRepository.saveAll(presaleDetWarehouseList);

        return findById(presaleRegister.Headboard.PresaleCod);
    }
    @Transactional
    public SaleDetailDto confirm(PresaleRegisterDto presaleRegister) {

        PresaleHeadEntity presale = this.presaleHeadRepository.findById(presaleRegister.Headboard.PresaleCod).get();

        presale.SaleStatus = "C"; //confirm
        presale.addSession(getUserCod(),false);

        this.presaleHeadRepository.save(presale);

        return this.saleService.save(findById(presaleRegister.Headboard.PresaleCod));
    }
    public PresaleDetailDto findById(String PresaleCod) {
        PresaleDetailDto  presaleDetail = new PresaleDetailDto();

        presaleDetail.Headboard = this.presaleHeadRepository.findById(PresaleCod).get();
        presaleDetail.DetailList = this.presaleDetRepository.findByPresaleCod(PresaleCod);

        for(var item : presaleDetail.DetailList)
        {
            item.DetailWarehouse = this.presaleDetWarehouseRepository.findByProductCod(item.PresaleCod,item.ProductCod);
            item.product = this.productShared.findById(item.ProductCod);
        }

        return presaleDetail;
    }
    public ResponseWsDto findDataForm() {
        ResponseWsDto rpt = new ResponseWsDto();

        rpt.AddResponseAdditional("CurrencySystem",this.currencyShared.findCurrencySystem());

        return rpt;
    }
    public ResponsePageSearch findAll(String Query, int Page,String StoreCod)
    {
        this.searchService = new SearchService(this.presaleHeadRepository);
        SearchDto search = new SearchDto(Query,Page,StoreCod);
        return this.searchService.findAllStore(search,10);
    }
    private List<PresaleDetWarehouseEntity> generateDetWarehouseDefault(PresaleRegisterDto presaleRegister, boolean IsNewPresale) {
        List<PresaleDetWarehouseEntity> presaleDetWarehouseList = new ArrayList<>();

        WarehouseEntity warehouseDefault = this.warehouseShared.findByStore(getStoreCod()).get(0);

        for(var product : presaleRegister.DetailList)
        {
            PresaleDetWarehouseEntity detWarehouse = new PresaleDetWarehouseEntity();
            detWarehouse.PresaleCod = presaleRegister.Headboard.PresaleCod;
            detWarehouse.ProductCod = product.ProductCod;
            detWarehouse.Variant = product.Variant;
            detWarehouse.WarehouseCod = warehouseDefault.WarehouseCod;
            detWarehouse.Status = "A";

            if( IsNewPresale )
            {
                detWarehouse.NumUnit = product.NumUnit;
                detWarehouse.addSession(getUserCod(),true);
                presaleDetWarehouseList.add(detWarehouse);
            }
            else
            {
                Optional<PresaleDetWarehouseEntity> detWarehouseOp = this.presaleDetWarehouseRepository.findById(
                        new PresaleDetWarehouseID(
                                detWarehouse.PresaleCod,
                                detWarehouse.ProductCod,
                                detWarehouse.Variant,
                                detWarehouse.WarehouseCod
                        )
                );

                if( detWarehouseOp.isPresent() )
                {
                    detWarehouse = detWarehouseOp.get();
                    detWarehouse.NumUnit = product.NumUnit;
                    detWarehouse.addSession(getUserCod(),false);
                    detWarehouse.Status = "A";
                }
                else
                {
                    detWarehouse.NumUnit = product.NumUnit;
                    detWarehouse.addSession(getUserCod(),true);
                }
                presaleDetWarehouseList.add(detWarehouse);
            }
        }
        return presaleDetWarehouseList;
    }
}
