package com.ccadmin.app.transfer;

import com.ccadmin.app.product.model.entity.ProductEntity;
import com.ccadmin.app.product.shared.KardexShared;
import com.ccadmin.app.product.shared.ProductShared;
import com.ccadmin.app.shared.model.dto.ResponseWsDto;
import com.ccadmin.app.store.model.entity.WarehouseEntity;
import com.ccadmin.app.store.repository.WarehouseRepository;
import com.ccadmin.app.store.shared.StoreShared;
import com.ccadmin.app.system.shared.CounterfoilShared;
import com.ccadmin.app.transfer.exception.TransferException;
import com.ccadmin.app.transfer.model.constants.TransferConstants;
import com.ccadmin.app.transfer.model.dto.TransferDispatchDto;
import com.ccadmin.app.transfer.model.dto.TransferReceiveDto;
import com.ccadmin.app.transfer.model.dto.TransferRegisterBundleDto;
import com.ccadmin.app.transfer.model.entity.TransferDetEntity;
import com.ccadmin.app.transfer.model.entity.TransferHeadEntity;
import com.ccadmin.app.transfer.repository.TransferDetRepository;
import com.ccadmin.app.transfer.repository.TransferDocumentRepository;
import com.ccadmin.app.transfer.repository.TransferHeadRepository;
import com.ccadmin.app.transfer.service.TransferCreateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferCreateServiceTest {

    @InjectMocks
    private TransferCreateService transferCreateService;

    @Mock
    private TransferHeadRepository transferHeadRepository;
    @Mock
    private TransferDetRepository transferDetRepository;
    @Mock
    private TransferDocumentRepository transferDocumentRepository;
    @Mock
    private ProductShared productShared;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private StoreShared storeShared;
    @Mock
    private KardexShared kardexShared;
    @Mock
    private CounterfoilShared counterfoilShared;

    @Test
    void createTe() throws Exception {
        TransferHeadEntity head = new TransferHeadEntity();
        head.TypeOperation = TransferConstants.TYPE_OPERATION_REQUEST;
        head.TransferCod = "TR001";
        head.StoreCodOrigin = "S001";
        head.StoreCodDest = "S002";

        TransferDetEntity det = new TransferDetEntity();
        det.ProductCod = "P001";
        det.Variant = "0000";
        det.NumUnit = 5;
        det.WarehouseCodOrigin = "WH1";
        det.WarehouseCodDest = "WH2";

        TransferRegisterBundleDto request = new TransferRegisterBundleDto();
        request.transferHead = head;
        request.transferDetList = List.of(det);

        ProductEntity product = new ProductEntity();
        product.ProductCod = "P001";
        product.Status = "A";

        WarehouseEntity whOrigin = new WarehouseEntity();
        whOrigin.WarehouseCod = "WH1";
        whOrigin.Status = "A";
        WarehouseEntity whDest = new WarehouseEntity();
        whDest.WarehouseCod = "WH2";
        whDest.Status = "A";

        when(productShared.findById("P001")).thenReturn(product);
        when(warehouseRepository.findById("WH1")).thenReturn(Optional.of(whOrigin));
        when(warehouseRepository.findById("WH2")).thenReturn(Optional.of(whDest));
        when(transferHeadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferDetRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRegisterBundleDto result = transferCreateService.create(request);

        Assertions.assertEquals("TR001", result.transferHead.TransferCod);
        Assertions.assertEquals(TransferConstants.STATUS_PENDING, result.transferHead.TransferStatus);
        Assertions.assertEquals(1, result.transferDetList.size());
    }

    @Test
    void createTsFromTe() throws Exception {
        TransferHeadEntity headTe = new TransferHeadEntity();
        headTe.TransferCod = "TR002";
        headTe.TypeOperation = TransferConstants.TYPE_OPERATION_REQUEST;
        headTe.StoreCodOrigin = "S001";
        headTe.StoreCodDest = "S002";

        TransferHeadEntity headTs = new TransferHeadEntity();
        headTs.TransferCod = "TR002";
        headTs.TypeOperation = TransferConstants.TYPE_OPERATION_SEND;
        headTs.StoreCodOrigin = "S001";
        headTs.StoreCodDest = "S002";

        TransferDetEntity teDet = new TransferDetEntity();
        teDet.TransferCod = "TR002";
        teDet.TypeOperation = TransferConstants.TYPE_OPERATION_REQUEST;
        teDet.ProductCod = "P002";
        teDet.Variant = "0000";
        teDet.NumUnit = 10;

        TransferDetEntity tsDet = new TransferDetEntity();
        tsDet.ProductCod = "P002";
        tsDet.Variant = "0000";
        tsDet.NumUnit = 10;

        TransferRegisterBundleDto request = new TransferRegisterBundleDto();
        request.transferHead = headTs;
        request.transferDetList = List.of(tsDet);

        ProductEntity product = new ProductEntity();
        product.ProductCod = "P002";
        product.Status = "A";

        when(transferHeadRepository.findByTransferCodAndTypeOperation("TR002", TransferConstants.TYPE_OPERATION_REQUEST))
                .thenReturn(headTe);
        when(transferHeadRepository.findByTransferCodAndTypeOperation("TR002", TransferConstants.TYPE_OPERATION_SEND))
                .thenReturn(null);
        when(transferDetRepository.findByTransferCodAndTypeOperation("TR002", TransferConstants.TYPE_OPERATION_REQUEST))
                .thenReturn(List.of(teDet));
        when(productShared.findById("P002")).thenReturn(product);
        when(transferHeadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferDetRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRegisterBundleDto result = transferCreateService.create(request);

        Assertions.assertEquals(TransferConstants.STATUS_PENDING, result.transferHead.TransferStatus);
        Assertions.assertEquals(1, result.transferDetList.size());
    }

    @Test
    void createTsWithoutTe() throws Exception {
        TransferHeadEntity headTs = new TransferHeadEntity();
        headTs.TransferCod = "TR006";
        headTs.TypeOperation = TransferConstants.TYPE_OPERATION_SEND;
        headTs.StoreCodOrigin = "S001";
        headTs.StoreCodDest = "S002";

        TransferDetEntity tsDet = new TransferDetEntity();
        tsDet.ProductCod = "P003";
        tsDet.Variant = "0000";
        tsDet.NumUnit = 7;

        TransferRegisterBundleDto request = new TransferRegisterBundleDto();
        request.transferHead = headTs;
        request.transferDetList = List.of(tsDet);

        ProductEntity product = new ProductEntity();
        product.ProductCod = "P003";
        product.Status = "A";

        when(transferHeadRepository.findByTransferCodAndTypeOperation("TR006", TransferConstants.TYPE_OPERATION_REQUEST))
                .thenReturn(null);
        when(transferHeadRepository.findByTransferCodAndTypeOperation("TR006", TransferConstants.TYPE_OPERATION_SEND))
                .thenReturn(null);
        when(transferDetRepository.findByTransferCodAndTypeOperation("TR006", TransferConstants.TYPE_OPERATION_REQUEST))
                .thenReturn(List.of());
        when(productShared.findById("P003")).thenReturn(product);
        when(transferHeadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferDetRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRegisterBundleDto result = transferCreateService.create(request);

        Assertions.assertEquals(TransferConstants.STATUS_PENDING, result.transferHead.TransferStatus);
        Assertions.assertEquals(1, result.transferDetList.size());
    }

    @Test
    void dispatchIdempotent() throws Exception {
        TransferHeadEntity headTs = new TransferHeadEntity();
        headTs.TransferCod = "TR003";
        headTs.TypeOperation = TransferConstants.TYPE_OPERATION_SEND;
        headTs.TransferStatus = TransferConstants.STATUS_DISPATCHED;

        when(transferHeadRepository.findByTransferCodAndTypeOperationForUpdate(
                "TR003", TransferConstants.TYPE_OPERATION_SEND
        )).thenReturn(headTs);

        TransferDispatchDto request = new TransferDispatchDto();
        request.transferCod = "TR003";
        request.transportModeCod = TransferConstants.TRANSPORT_PUBLIC;
        request.carrierRuc = "20100011122";
        request.carrierName = "Carrier";

        ResponseWsDto response = transferCreateService.dispatchTransfer(request);

        Assertions.assertEquals("La transferencia ya fue despachada", response.Message);
    }

    @Test
    void receiveIdempotent() throws Exception {
        TransferHeadEntity headTs = new TransferHeadEntity();
        headTs.TransferCod = "TR004";
        headTs.TypeOperation = TransferConstants.TYPE_OPERATION_SEND;
        headTs.TransferStatus = TransferConstants.STATUS_FINALIZED;

        when(transferHeadRepository.findByTransferCodAndTypeOperationForUpdate(
                "TR004", TransferConstants.TYPE_OPERATION_SEND
        )).thenReturn(headTs);

        TransferReceiveDto request = new TransferReceiveDto();
        request.transferCod = "TR004";

        ResponseWsDto response = transferCreateService.receiveTransfer(request);

        Assertions.assertEquals("La transferencia ya fue recibida", response.Message);
    }

    @Test
    void transportValidation() {
        TransferHeadEntity headTs = new TransferHeadEntity();
        headTs.TransferCod = "TR005";
        headTs.TypeOperation = TransferConstants.TYPE_OPERATION_SEND;
        headTs.TransferStatus = TransferConstants.STATUS_PENDING;

        when(transferHeadRepository.findByTransferCodAndTypeOperationForUpdate(
                "TR005", TransferConstants.TYPE_OPERATION_SEND
        )).thenReturn(headTs);

        TransferDispatchDto request = new TransferDispatchDto();
        request.transferCod = "TR005";
        request.transportModeCod = TransferConstants.TRANSPORT_PUBLIC;

        Assertions.assertThrows(TransferException.class, () -> transferCreateService.dispatchTransfer(request));
    }
}
