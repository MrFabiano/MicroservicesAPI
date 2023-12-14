package com.microservices.apis.controller;

import com.microservices.apis.model.Profissao;
import com.microservices.apis.repository.ProfissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/profession")
@CrossOrigin(origins = "*")
public class ProfessionController {

    @Autowired
    private ProfissaoRepository profissaoRepository;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Profissao>> listProfissao(){
        List<Profissao> list = profissaoRepository.findAll();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
