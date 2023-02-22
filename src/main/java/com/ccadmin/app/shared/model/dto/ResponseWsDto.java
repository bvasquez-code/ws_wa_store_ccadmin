package com.ccadmin.app.shared.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ResponseWsDto {
    public String Status;
    public String Message;
    public Object Data;
    public boolean ErrorStatus;
    public long ErrorID;
    public List<ResponseAdditionalDto> DataAdditional;

    public ResponseWsDto()
    {
        this.DataAdditional = new ArrayList<>();
    }
    public ResponseWsDto(Object Data)
    {
        this.Data = Data;
        this.Ok();
    }

    public ResponseWsDto(Exception ex)
    {
        this.Data = ex;
        this.Error();
        this.Message = ex.getMessage();
    }
    public ResponseWsDto(String Message)
    {
        this.Message = Message;
        this.Ok();
    }

    private void Ok()
    {
        this.Status = "200";
        this.Message = "operation performed successfully";
    }

    private void Error()
    {
        this.Status = "500";
        this.Message = "Error : An unexpected error occurred";
        this.ErrorStatus = true;
    }

    private void Error(String Message)
    {
        this.Status = "500";
        this.Message = Message;
        this.ErrorStatus = true;
    }

    public void AddResponseAdditional(String name, Object data)
    {
        this.DataAdditional.add( new ResponseAdditionalDto(name,data) );
    }
}
