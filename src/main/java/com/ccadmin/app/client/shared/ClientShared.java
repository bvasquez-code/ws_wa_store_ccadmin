package com.ccadmin.app.client.shared;

import com.ccadmin.app.client.model.entity.ClientEntity;
import com.ccadmin.app.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientShared {

    @Autowired
    private ClientService clientService;

    public ClientEntity findById(String ClientCod)
    {
        return this.clientService.findById(ClientCod);
    }
}
