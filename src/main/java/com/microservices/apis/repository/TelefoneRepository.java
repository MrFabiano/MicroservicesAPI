package com.microservices.apis.repository;

import com.microservices.apis.model.Telefone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {


}
