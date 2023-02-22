package com.ccadmin.app.product.shared;

import com.ccadmin.app.product.model.entity.ProductInfoWarehouseEntity;
import com.ccadmin.app.product.model.entity.id.ProductInfoWarehouseId;
import com.ccadmin.app.product.service.ProductInfoWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductInfoWarehouseShared {

    @Autowired
    private ProductInfoWarehouseService productInfoWarehouseService;

    public ProductInfoWarehouseEntity findById(ProductInfoWarehouseId id)
    {
        return this.productInfoWarehouseService.findById(id);
    }

    public List<ProductInfoWarehouseEntity> saveAll(List<ProductInfoWarehouseEntity> list)
    {
        return this.productInfoWarehouseService.saveAll(list);
    }

}
