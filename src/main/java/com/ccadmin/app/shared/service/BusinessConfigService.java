package com.ccadmin.app.shared.service;

import com.ccadmin.app.shared.model.entity.BusinessConfigEntity;
import com.ccadmin.app.shared.model.entity.id.BusinessConfigEntityID;
import com.ccadmin.app.shared.repository.BusinessConfigRepository;
import com.ccadmin.app.system.model.dto.DocumentTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessConfigService {

    @Autowired
    private BusinessConfigRepository businessConfigRepository;

    public BusinessConfigEntity findById(BusinessConfigEntityID id)
    {
        return this.businessConfigRepository.findById(id).get();
    }

    public List<BusinessConfigEntity> findAllById(List<BusinessConfigEntityID> businessConfigIDList){
        return this.businessConfigRepository.findAllById(businessConfigIDList);
    }

    public BusinessConfigEntity findByConfigCod( String GroupCod,String ConfigCod){
        return this.businessConfigRepository.findByConfigCod(GroupCod,ConfigCod);
    }

    public List<BusinessConfigEntity> findByGroupCod(String GroupCod){
        return this.businessConfigRepository.findByGroupCod(GroupCod);
    }

    public List<DocumentTypeDto> getSaleDocumentType(){
        return this.findByGroupCod("SalesDocumentType")
                .stream()
                .map( e -> new DocumentTypeDto(e.ConfigCod,e.ConfigVal))
                .toList();
    }

}
