package com.ccadmin.app.system.shared;

import com.ccadmin.app.sale.model.entity.SaleDocumentEntity;
import com.ccadmin.app.system.service.CounterfoilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounterfoilShared {

    @Autowired
    private CounterfoilService counterfoilService;

    public SaleDocumentEntity generateDocumentSale(String StoreCod, String DocumentType)
    {
        return this.counterfoilService.generateDocumentSale(StoreCod,DocumentType);
    }
}
