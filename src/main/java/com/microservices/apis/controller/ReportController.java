package com.microservices.apis.controller;

import com.microservices.apis.model.UserReport;
import com.microservices.apis.service.RelatorioService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
@CrossOrigin(origins = "*")
public class ReportController {

    private final RelatorioService relatorioService;

    public ReportController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @PostMapping(value= "/", produces = "application/text")
    public ResponseEntity<String> downloadReportParam(HttpServletRequest request, @RequestBody UserReport userReport) {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");

            String dataInicio = dateFormatParam.format(dateFormat.parse(userReport.getDataInicio()));
            String dataFim = dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));

            Map<String,Object> params = new HashMap<>();
            params.put("DATA_INICIO", dataInicio);
            params.put("DATA_FIM", dataFim);
            byte[] pdf = relatorioService.gerarReportPrint("relatorio-usuario-param", params,
                    request.getServletContext());
            String base64 = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
            return new ResponseEntity<>(base64, HttpStatus.OK);

        }catch (Exception e){
            throw new RuntimeException("It was not possible to issue the report");
        }
    }

    @GetMapping(value = "/", produces = "application/text")
    public ResponseEntity<String> downloadReport(HttpServletRequest request) throws Exception {
        Map<String,Object> params = new HashMap<>();
        byte[] pdf = relatorioService.gerarReport("relatorio-usuario",
                params, request.getServletContext());

        String base64 = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
        return new ResponseEntity<>(base64, HttpStatus.OK);

    }
}
