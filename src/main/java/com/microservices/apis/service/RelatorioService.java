package com.microservices.apis.service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Locale;

@Service
public class RelatorioService {

    public byte[] gerarRelatorio(String relatorio, ServletContext servletContext) throws Exception {

        try {
            var inputStream = this.getClass().getResourceAsStream(
                    "/relatorios/relatorio-usuario.jasper");
            var parametros = new HashMap<String, Object>();
            parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

            var jasperPrint = JasperFillManager.fillReport(inputStream, parametros);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        }catch (Exception e){
            throw new RuntimeException("Não foi possivel emitir relatório");
        }
    }
}
