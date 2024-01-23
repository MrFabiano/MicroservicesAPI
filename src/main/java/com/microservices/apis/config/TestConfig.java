//package com.microservices.apis.config;
//
//import com.microservices.apis.service.DBService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import javax.annotation.PostConstruct;
//
//@Configuration
//@Profile("test")
//public class TestConfig {
//
//    @Autowired
//    private DBService dbService;
//
//    @PostConstruct
//    public void instanciaBancoDados(){
//        this.dbService.instanciaBancoDados();
//    }
//}
