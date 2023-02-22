package com.ccadmin.app.product.service;

import com.ccadmin.app.product.model.entity.BrandEntity;
import com.ccadmin.app.product.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public BrandEntity findById(String brandCod)
    {
        return this.brandRepository.findById(brandCod).get();
    }

}
