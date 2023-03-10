package com.ccadmin.app.person.shared;

import com.ccadmin.app.person.model.entity.PersonEntity;
import com.ccadmin.app.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonShared {

    @Autowired
    private PersonService personService;

    public PersonEntity save(PersonEntity person)
    {
        return this.personService.save(person);
    }

    public PersonEntity findById(String PersonCod)
    {
        return this.personService.findById(PersonCod);
    }
}
