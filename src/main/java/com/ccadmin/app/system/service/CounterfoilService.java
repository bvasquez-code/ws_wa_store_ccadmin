package com.ccadmin.app.system.service;

import com.ccadmin.app.sale.model.entity.SaleDocumentEntity;
import com.ccadmin.app.system.model.entity.CounterfoilEntity;
import com.ccadmin.app.system.repository.CounterfoilRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounterfoilService {

    @Autowired
    private CounterfoilRepository counterfoilRepository;

    public SaleDocumentEntity generateDocumentSale(String StoreCod, String DocumentType)
    {
        SaleDocumentEntity saleDocument = new SaleDocumentEntity();
        CounterfoilEntity counterfoil = this.counterfoilRepository.findByStoreDefault(DocumentType,StoreCod);

        counterfoil.Correlative = counterfoil.Correlative + 1;
        this.counterfoilRepository.save(counterfoil);

        int Correlative = 1000000 + counterfoil.Correlative;

        saleDocument.DocumentCod = counterfoil.Series + "-" + String.valueOf(Correlative).substring(1,7);
        saleDocument.CounterfoilCod = counterfoil.CounterfoilCod;
        return saleDocument;
    }
}
