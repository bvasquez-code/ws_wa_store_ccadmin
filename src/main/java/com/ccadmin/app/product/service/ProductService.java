package com.ccadmin.app.product.service;

import com.ccadmin.app.product.model.dto.ProductInfoDto;
import com.ccadmin.app.product.model.dto.ProductRegisterDto;
import com.ccadmin.app.product.model.dto.ProductRegisterMassiveDto;
import com.ccadmin.app.product.model.entity.ProductEntity;
import com.ccadmin.app.product.model.entity.ProductVariantEntity;
import com.ccadmin.app.product.repository.*;
import com.ccadmin.app.shared.service.SessionService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService extends SessionService {

    public static Logger log = LogManager.getLogger(SessionService.class);
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductConfigRepository productConfigRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductInfoRepository productInfoRepository;
    @Autowired
    private ProductInfoWarehouseRepository productInfoWarehouseRepository;
    @Autowired
    private ProductPictureRepository productPictureRepository;


    public List<ProductEntity> findAll()
    {
        return this.productRepository.findAll();
    }

    public ProductEntity findById(String ProductCod)
    {
        return this.productRepository.findById(ProductCod).get();
    }

    @Transactional
    public ProductRegisterDto save(ProductRegisterDto productRegister)
    {
        productRegister.product.addSessionCreate(getUserCod());
        productRegister.config.addSessionCreate(getUserCod());

        ProductVariantEntity variant = new ProductVariantEntity(productRegister.product.ProductCod);
        variant.addSessionCreate(getUserCod());

        productRegister.config.ProductCod = productRegister.product.ProductCod;

        this.productRepository.save(productRegister.product);
        this.productConfigRepository.save(productRegister.config);
        this.productVariantRepository.save(variant);
        this.productInfoRepository.saveAllInfo(productRegister.product.ProductCod);
        this.productInfoWarehouseRepository.saveAllInfo(productRegister.product.ProductCod);

        return productRegister;
    }

    public ProductInfoDto findDetailById(String ProductCod,String StoreCod)
    {
        ProductInfoDto productInfoDto = new ProductInfoDto();

        productInfoDto.Product = productRepository.findById(ProductCod).get();
        productInfoDto.Config = productConfigRepository.findById(ProductCod).get();
        productInfoDto.VariantList = productVariantRepository.findAllVariantProduct(ProductCod);
        productInfoDto.InfoList = productInfoRepository.findInfoStore(ProductCod,StoreCod);
        productInfoDto.InfoWarehouseList = productInfoWarehouseRepository.findInfoWarehouse(StoreCod,ProductCod);
        productInfoDto.Picture = productPictureRepository.findPrincipal(ProductCod);
        return productInfoDto;
    }

    @Transactional
    public int saveAll(ProductRegisterMassiveDto productRegisterMassive)
    {
        int numRegister = 0;

        for(var item : productRegisterMassive.productList)
        {
            save(item);
            numRegister++;
        }

        return numRegister;
    }
}
