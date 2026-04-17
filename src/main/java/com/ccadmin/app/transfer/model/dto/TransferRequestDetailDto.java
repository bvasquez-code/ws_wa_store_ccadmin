package com.ccadmin.app.transfer.model.dto;

import com.ccadmin.app.transfer.model.entity.*;

import java.util.List;

public class TransferRequestDetailDto {
    public TransferRequestHeadEntity transferHeadTe;
    public TransferHeadEntity transferHeadTs;
    public List<TransferRequestDetEntity> transferDetTeList;
    public List<TransferDetEntity> transferDetTsList;
    public List<TransferDocumentEntity> transferDocumentList;
}
