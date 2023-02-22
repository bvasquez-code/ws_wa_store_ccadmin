package com.ccadmin.app.product.shared;

import com.ccadmin.app.product.model.entity.ProductEntity;
import com.ccadmin.app.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductShared {

    @Autowired
    private ProductService productService;

    public ProductEntity findById(String ProductCod)
    {
        return this.productService.findById(ProductCod);
    }
}
