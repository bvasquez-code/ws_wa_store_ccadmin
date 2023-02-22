package com.ccadmin.app.product.service;

import com.ccadmin.app.product.model.entity.ProductInfoWarehouseEntity;
import com.ccadmin.app.product.model.entity.id.ProductInfoWarehouseId;
import com.ccadmin.app.product.repository.ProductInfoWarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductInfoWarehouseService {

    @Autowired
    private ProductInfoWarehouseRepository productInfoWarehouseRepository;

    public ProductInfoWarehouseEntity findById(ProductInfoWarehouseId id)
    {
        return this.productInfoWarehouseRepository.findById(id).get();
    }

    public List<ProductInfoWarehouseEntity> saveAll(List<ProductInfoWarehouseEntity> list)
    {
        return this.productInfoWarehouseRepository.saveAll(list);
    }
}
