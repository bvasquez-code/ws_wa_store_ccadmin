package com.ccadmin.app.sale.service;

import com.ccadmin.app.product.shared.ProductShared;
import com.ccadmin.app.sale.model.dto.PresaleDetailDto;
import com.ccadmin.app.sale.model.dto.PresaleRegisterDto;
import com.ccadmin.app.sale.model.dto.SaleDetailDto;
import com.ccadmin.app.sale.model.entity.PresaleDetWarehouseEntity;
import com.ccadmin.app.sale.model.entity.PresaleHeadEntity;
import com.ccadmin.app.sale.model.entity.id.PresaleDetWarehouseID;
import com.ccadmin.app.sale.repository.PresaleDetRepository;
import com.ccadmin.app.sale.repository.PresaleDetWarehouseRepository;
import com.ccadmin.app.sale.repository.PresaleHeadRepository;
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
    private CurrencyShared currencyShared;

    @Autowired
    private WarehouseShared warehouseShared;

    @Autowired
    private ProductShared productShared;

    @Autowired
    private SaleService saleService;

    @Transactional
    public PresaleDetailDto save(PresaleRegisterDto presaleRegister)
    {
        WarehouseEntity warehouseUnit = new WarehouseEntity();
        List<PresaleDetWarehouseEntity> presaleDetWarehouseList = new ArrayList<>();
        boolean isNew = ( presaleRegister.Headboard.PresaleCod == null ||  presaleRegister.Headboard.PresaleCod.trim().isEmpty() == true);
        boolean IsMultipleWarehouse = warehouseShared.IsMultipleWarehouse(presaleRegister.Headboard.StoreCod);

        if( isNew  )
        {
            presaleRegister.Headboard.PresaleCod = presaleHeadRepository.getPresaleCod(getStoreCod());
        }
        else
        {
            this.presaleDetRepository.updateStatusAll(presaleRegister.Headboard.PresaleCod,"I");
            this.presaleDetWarehouseRepository.updateStatusAll(presaleRegister.Headboard.PresaleCod,"I");
        }

        if (!IsMultipleWarehouse)
        {
            warehouseUnit = this.warehouseShared.findByStore(getStoreCod()).get(0);
        }

        CurrencyEntity currencySystem = this.currencyShared.findCurrencySystem();

        presaleRegister.Headboard.addSession(getUserCod(),isNew);
        presaleRegister.Headboard.StoreCod = getStoreCod();
        presaleRegister.Headboard.CurrencyCodSys = currencySystem.CurrencyCod;
        presaleRegister.Headboard.NumExchangevalue = new BigDecimal(1);

        if(!presaleRegister.Headboard.CurrencyCodSys.equals(presaleRegister.Headboard.CurrencyCod))
        {
            CurrencyEntity currencyPucharse = this.currencyShared.findById(presaleRegister.Headboard.CurrencyCod);
            presaleRegister.Headboard.NumExchangevalue = currencyPucharse.NumExchangevalue;
        }

        for(var product : presaleRegister.DetailList)
        {
            product.addSession(getUserCod(),isNew);
            product.PresaleCod = presaleRegister.Headboard.PresaleCod;
            product.NumUnitPriceSale = product.NumUnitPrice.subtract( product.NumDiscount );
            product.NumTotalPrice = product.NumUnitPriceSale.multiply(new BigDecimal(product.NumUnit));

            if( product.DetailWarehouse == null || product.DetailWarehouse.size() > 0)
            {
                boolean DetailWarehouseIsNew = isNew;
                PresaleDetWarehouseEntity detWarehouse = new PresaleDetWarehouseEntity();

                if( !isNew )
                {
                    Optional<PresaleDetWarehouseEntity>  detWarehouseOp = this.presaleDetWarehouseRepository.findById(
                            new PresaleDetWarehouseID(presaleRegister.Headboard.PresaleCod,product.ProductCod,product.Variant,warehouseUnit.WarehouseCod)
                    );
                    if (detWarehouseOp.isPresent())
                    {
                        detWarehouse = detWarehouseOp.get();
                    }
                    else
                    {
                        DetailWarehouseIsNew = true;
                    }
                }

                detWarehouse.PresaleCod = presaleRegister.Headboard.PresaleCod;
                detWarehouse.ProductCod = product.ProductCod;
                detWarehouse.Variant = product.Variant;
                detWarehouse.WarehouseCod = warehouseUnit.WarehouseCod;
                detWarehouse.NumUnit = product.NumUnit;
                detWarehouse.addSession(getUserCod(),DetailWarehouseIsNew);
                detWarehouse.Status = "A";
                presaleDetWarehouseList.add(detWarehouse);
            }
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

    public PresaleDetailDto findById(String PresaleCod)
    {
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
}
