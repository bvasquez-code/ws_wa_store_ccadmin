package com.ccadmin.app.product.service;

import com.ccadmin.app.product.model.dto.ProductSearchDto;
import com.ccadmin.app.product.model.entity.ProductSearchEntity;
import com.ccadmin.app.product.model.entity.id.ProductSearchID;
import com.ccadmin.app.product.repository.ProductSearchRepository;
import com.ccadmin.app.shared.model.dto.ResponsePageSearch;
import com.ccadmin.app.shared.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductSearchService {

    private static int limitSearchProduct = 12;
    @Autowired
    private ProductSearchRepository productSearchRepository;
    private SearchService searchService;

    public ProductSearchEntity findById(ProductSearchID id)
    {
        return this.productSearchRepository.findById(id).get();
    }


    public ResponsePageSearch query(ProductSearchDto productSearch)
    {
        this.searchService = new SearchService(this.productSearchRepository);
        return this.searchService.findAllStore(productSearch,limitSearchProduct);
    }
}
