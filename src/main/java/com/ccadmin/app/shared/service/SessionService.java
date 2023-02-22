package com.ccadmin.app.shared.service;

import com.ccadmin.app.shared.model.dto.SessionDto;
import com.ccadmin.app.user.shared.UserStoreShared;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SessionService {

    @Autowired
    protected UserStoreShared userStoreShared;
    protected SessionDto sessionDto = new SessionDto();

    public String getUserCod()
    {
        if(sessionDto.UserCod ==null || sessionDto.UserCod.isEmpty())
        {
            sessionDto.UserCod = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }
        return sessionDto.UserCod;
    }

    public String getStoreCod()
    {
        if(sessionDto.StoreCod ==null || sessionDto.StoreCod.isEmpty())
        {
            sessionDto.StoreCod = this.userStoreShared.getMainStore(getUserCod());
        }
        return sessionDto.StoreCod;
    }

}
