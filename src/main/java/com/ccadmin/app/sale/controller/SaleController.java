package com.ccadmin.app.sale.controller;

import com.ccadmin.app.sale.model.dto.SalePaymentDto;
import com.ccadmin.app.sale.model.entity.SalePaymentEntity;
import com.ccadmin.app.sale.service.SaleService;
import com.ccadmin.app.shared.model.dto.ResponseWsDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sale")
public class SaleController {

    public static Logger log = LogManager.getLogger(SaleController.class);
    @Autowired
    private SaleService saleService;


    @PostMapping("addPayment")
    public ResponseEntity<ResponseWsDto> addPayment(@RequestBody SalePaymentDto salePayment)
    {
        try{
            return new ResponseEntity<ResponseWsDto>(
                    new ResponseWsDto(this.saleService.addPayment(salePayment))
                    , HttpStatus.OK
            );
        }
        catch (Exception ex)
        {
            log.error("Error :"+ex.getMessage(), ex);
            return new ResponseEntity<ResponseWsDto>(new ResponseWsDto(ex),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("findDataForm")
    public ResponseEntity<ResponseWsDto> findDataForm(@RequestParam String SaleCod)
    {
        try{
            return new ResponseEntity<ResponseWsDto>(
                    this.saleService.findDataForm(SaleCod)
                    ,HttpStatus.OK
            );
        }
        catch (Exception ex)
        {
            return new ResponseEntity<ResponseWsDto>(new ResponseWsDto(ex),HttpStatus.BAD_REQUEST);
        }
    }

}
