package com.ccadmin.app.person.repository;

import com.ccadmin.app.person.model.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity,String> {
}
