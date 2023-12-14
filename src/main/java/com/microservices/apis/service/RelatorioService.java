package com.microservices.apis.service;


import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.*;

@Service
public class RelatorioService implements Serializable {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public byte[] gerarReport(String relatorio, Map<String, Object> params, ServletContext servletContext) throws JRException {
        try {
            Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
            // Create the JasperPrint for the first report
            InputStream reportStream = getClass().getResourceAsStream("/relatorios/relatorio-usuario.jasper");
            var parametros = new HashMap<String, Object>();
            parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

            var jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("It was not possible to issue the report");
        }
    }

    public byte[] gerarReportPrint(String relatorio, Map<String, Object> params, ServletContext servletContext) {
        try {
            Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
            // Create the JasperPrint for the first report
            InputStream reportStream = getClass().getResourceAsStream("/relatorios/relatorio-usuario-param.jasper");
            var parametros = new HashMap<String, Object>();
            parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
            if (connection != null) {
            var jasperPrint = JasperFillManager.fillReport(reportStream, parametros,(Connection)null);
            return JasperExportManager.exportReportToPdf(jasperPrint);
           }
        } catch (Exception e) {
            throw new RuntimeException("It was not possible to issue the report");
        }
        return new byte[1];
    }
}




