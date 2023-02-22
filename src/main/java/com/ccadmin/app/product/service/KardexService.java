package com.ccadmin.app.product.service;

import com.ccadmin.app.product.model.entity.KardexEntity;
import com.ccadmin.app.product.repository.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KardexService {

    @Autowired
    private KardexRepository kardexRepository;

    public KardexEntity save(KardexEntity kardex)
    {
        return this.kardexRepository.save(kardex);
    }

    public List<KardexEntity> saveAll(List<KardexEntity> kardexList)
    {
        return this.kardexRepository.saveAll(kardexList);
    }

    public KardexEntity findLastMovement(String ProductCod,String WarehouseCod,String StoreCod)
    {
        return this.kardexRepository.findLastMovement(ProductCod,WarehouseCod,StoreCod);
    }
}
