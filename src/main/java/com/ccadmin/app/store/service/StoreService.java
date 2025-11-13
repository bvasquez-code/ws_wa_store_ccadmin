package com.ccadmin.app.store.service;

import com.ccadmin.app.store.model.dto.StoreInfoDto;
import com.ccadmin.app.store.model.entity.StoreEntity;
import com.ccadmin.app.store.repository.CompanyRepository;
import com.ccadmin.app.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CompanyRepository companyRepository;

    public StoreEntity findById(String StoreCod)
    {
        return this.storeRepository.findById(StoreCod).get();
    }

    public List<StoreEntity> findAll()
    {
        return this.storeRepository.findAll();
    }

    public String findUbigeo(String UbigeoCod){
        return this.storeRepository.findUbigeo(UbigeoCod);
    }

    public StoreInfoDto findStoreInfo(String StoreCod){
        StoreInfoDto storeInfo = new StoreInfoDto();
        storeInfo.Company = this.companyRepository.findMyCompany();
        storeInfo.CompanyUbigeo = this.storeRepository.findUbigeo(storeInfo.Company.UbigeoCod);
        storeInfo.Store = this.storeRepository.findById(StoreCod).get();
        storeInfo.StoreUbigeo = this.storeRepository.findUbigeo(storeInfo.Store.UbigeoCod);
        return storeInfo;
    }
}
