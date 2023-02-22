package com.ccadmin.app.pucharse.service;

import com.ccadmin.app.product.model.entity.KardexEntity;
import com.ccadmin.app.product.model.entity.ProductInfoEntity;
import com.ccadmin.app.product.model.entity.ProductInfoWarehouseEntity;
import com.ccadmin.app.product.model.entity.id.ProductInfoId;
import com.ccadmin.app.product.model.entity.id.ProductInfoWarehouseId;
import com.ccadmin.app.product.shared.KardexShared;
import com.ccadmin.app.product.shared.ProductInfoShared;
import com.ccadmin.app.product.shared.ProductInfoWarehouseShared;
import com.ccadmin.app.pucharse.exception.PucharseException;
import com.ccadmin.app.pucharse.model.dto.PucharseDetailsDto;
import com.ccadmin.app.pucharse.model.dto.PucharseRegisterDto;
import com.ccadmin.app.pucharse.model.entity.*;
import com.ccadmin.app.pucharse.repository.*;
import com.ccadmin.app.shared.service.SessionService;
import com.ccadmin.app.store.model.entity.WarehouseEntity;
import com.ccadmin.app.store.shared.WarehouseShared;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PucharseService extends SessionService {

    public static Logger log = LogManager.getLogger(PucharseService.class);
    @Autowired
    private PucharseHeadRepository pucharseHeadRepository;
    @Autowired
    private PucharseDetRepository pucharseDetRepository;
    @Autowired
    private PucharseRequestHeadRepository pucharseRequestHeadRepository;
    @Autowired
    private PucharseRequestDetRepository pucharseRequestDetRepository;
    @Autowired
    private PucharseDetDeliveryRepository pucharseDetDeliveryRepository;
    @Autowired
    private KardexShared kardexShared;
    @Autowired
    private ProductInfoWarehouseShared productInfoWarehouseShared;
    @Autowired
    private ProductInfoShared productInfoShared;
    @Autowired
    private WarehouseShared warehouseShared;

    @Transactional
    public PucharseDetailsDto save(PucharseRegisterDto pucharseRegister) throws Exception {

        PucharseRequestHeadEntity headRequest = pucharseRequestHeadRepository.findById(pucharseRegister.PucharseReqCod).get();
        List<PucharseRequestDetEntity> detailRequestList = pucharseRequestDetRepository.findAllActive(pucharseRegister.PucharseReqCod);

        if( headRequest.PurchaseStatus.equals("A") )
        {
            throw new PucharseException("¡Request is approved!");
        }

        PucharseHeadEntity head = new PucharseHeadEntity(headRequest);
        head.addSession(getUserCod(),true);
        head.PucharseCod = this.pucharseHeadRepository.getPucharseCod(getStoreCod());
        head.PurchaseStatus = "P";
        List<PucharseDetEntity> detailList = new ArrayList<>();

        for (var item : detailRequestList)
        {
            PucharseDetEntity pucharseDet = new PucharseDetEntity(item);
            pucharseDet.addSession(getUserCod(),true);
            pucharseDet.PucharseCod = head.PucharseCod;
            detailList.add(pucharseDet);
        }

        this.pucharseHeadRepository.save(head);
        this.pucharseDetRepository.saveAll(detailList);

        headRequest.PurchaseStatus = "A";
        this.pucharseRequestHeadRepository.save(headRequest);

        return findById(head.PucharseCod);
    }

    public PucharseDetailsDto findById(String PucharseCod)
    {
        PucharseDetailsDto pucharseDetails = new PucharseDetailsDto();

        pucharseDetails.Headboard = this.pucharseHeadRepository.findById(PucharseCod).get();
        pucharseDetails.DetailList = this.pucharseDetRepository.findAllActive(PucharseCod);

        return pucharseDetails;
    }

    @Transactional
    public PucharseDetailsDto confirm(PucharseRegisterDto pucharseRegister) throws Exception {

        PucharseHeadEntity Headboard = this.pucharseHeadRepository.findById(pucharseRegister.PucharseCod).get();
        List<PucharseDetEntity> DetailList = this.pucharseDetRepository.findAllActive(pucharseRegister.PucharseCod);
        List<KardexEntity> kardexList = new ArrayList<>();
        List<ProductInfoEntity> productInfoList = new ArrayList<>();
        List<ProductInfoWarehouseEntity> productWarehouseList = new ArrayList<>();
        List<PucharseDetDeliveryEntity> DeliveryList = new ArrayList<>();
        WarehouseEntity warehouseUnit = new WarehouseEntity();

        boolean IsMultipleWarehouse = warehouseShared.IsMultipleWarehouse(Headboard.StoreCod);

        if (!IsMultipleWarehouse)
        {
            warehouseUnit = this.warehouseShared.findByStore(Headboard.StoreCod).get(0);
        }

        if( Headboard.PurchaseStatus.equals("E") )
        {
            throw new PucharseException("purchase has already been delivered");
        }

        for(var item : DetailList)
        {

            List<PucharseDetDeliveryEntity> detailWarehouseCod = new ArrayList<>();

            if( IsMultipleWarehouse )
            {
                detailWarehouseCod = pucharseRegister.DeliveryList.stream().filter(
                        e-> e.ProductCod.equals(item.ProductCod) && e.Variant.equals(item.Variant)
                ).toList();
            }
            else
            {
                PucharseDetDeliveryEntity detDelivery = new PucharseDetDeliveryEntity();
                detDelivery.PucharseCod = pucharseRegister.PucharseCod;
                detDelivery.ProductCod = item.ProductCod;
                detDelivery.Variant = item.Variant;
                detDelivery.NumUnit = item.NumUnit;
                detDelivery.WarehouseCod = warehouseUnit.WarehouseCod;
                detailWarehouseCod.add(detDelivery);
            }

            for(var itemWarehouse : detailWarehouseCod )
            {
                int NumStockBefore = 0;

                itemWarehouse.PucharseCod = pucharseRegister.PucharseCod;
                itemWarehouse.addSession(getUserCod(),true);

                KardexEntity kardexLast = this.kardexShared.findLastMovement(item.ProductCod,itemWarehouse.WarehouseCod,Headboard.StoreCod);

                if( kardexLast != null ) NumStockBefore = kardexLast.NumStockAfter;

                KardexEntity kardex = new KardexEntity();
                kardex.OperationCod = Headboard.PucharseCod;
                kardex.SourceTable = "pucharse_head";
                kardex.TypeOperation = "S";
                kardex.ProductCod = item.ProductCod;
                kardex.Variant = item.Variant;
                kardex.StoreCod = Headboard.StoreCod;
                kardex.WarehouseCod = itemWarehouse.WarehouseCod;
                kardex.NumStockBefore = NumStockBefore;
                kardex.NumStockMoved = itemWarehouse.NumUnit;
                kardex.NumStockAfter = NumStockBefore + itemWarehouse.NumUnit;
                kardex.addSession(getUserCod(),true);
                kardexList.add(kardex);

                ProductInfoWarehouseEntity productInfoWarehouse = this.productInfoWarehouseShared.findById(
                        new ProductInfoWarehouseId(item.ProductCod,item.Variant,itemWarehouse.WarehouseCod)
                );
                productInfoWarehouse.NumDigitalStock = productInfoWarehouse.NumDigitalStock + itemWarehouse.NumUnit;
                productInfoWarehouse.NumPhysicalStock = productInfoWarehouse.NumPhysicalStock + itemWarehouse.NumUnit;
                productInfoWarehouse.addSession(getUserCod(),false);
                productWarehouseList.add(productInfoWarehouse);

                DeliveryList.add(itemWarehouse);
            }

            ProductInfoEntity productInfo = this.productInfoShared.findById(
                    new ProductInfoId(item.ProductCod,item.Variant,Headboard.StoreCod)
            );
            productInfo.NumDigitalStock = productInfo.NumDigitalStock + item.NumUnit;
            productInfo.NumPhysicalStock = productInfo.NumPhysicalStock + item.NumUnit;
            productInfo.addSession(getUserCod(),false);
            productInfoList.add(productInfo);
        }

        Headboard.PurchaseStatus = "E";
        Headboard.addSession(getUserCod(),false);
        this.pucharseHeadRepository.save(Headboard);
        this.pucharseDetDeliveryRepository.saveAll(DeliveryList);
        this.kardexShared.saveAll(kardexList);
        this.productInfoShared.saveAll(productInfoList);
        this.productInfoWarehouseShared.saveAll(productWarehouseList);

        return findById(Headboard.PucharseCod);
    }

}
