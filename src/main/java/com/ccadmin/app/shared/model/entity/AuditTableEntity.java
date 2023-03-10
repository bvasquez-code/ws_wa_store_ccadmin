package com.ccadmin.app.shared.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class AuditTableEntity  {

    public String CreationUser;
    public Date CreationDate;
    public String ModifyUser;
    public Date ModifyDate;
    public String Status;

    public AuditTableEntity()
    {
        this.CreationDate = new Date();
        this.ModifyDate = new Date();
        this.Status = "A";
    }

    public void addSessionModify(String userCod)
    {
        this.ModifyDate = new Date();
        this.ModifyUser = userCod;
    }

    public void addSessionCreate(String userCod)
    {
        this.CreationDate = new Date();
        this.CreationUser = userCod;
    }

    public void addSession(String userCod,boolean isNewRegister)
    {
        if(isNewRegister)
        {
            addSessionCreate(userCod);
        }else
        {
            addSessionModify(userCod);
        }
    }

}
