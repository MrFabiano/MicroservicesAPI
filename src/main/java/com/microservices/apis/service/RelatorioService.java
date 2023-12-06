package com.microservices.apis.service;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


@Service
@AllArgsConstructor
public class RelatorioService implements Serializable {

    private final JdbcTemplate jdbcTemplate;

    public byte[] gerarReport(String relatorio, Map<String, Object> params, ServletContext servletContext) throws JRException {
        try{
       Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        // Create the JasperPrint for the first report
        InputStream reportStream = getClass().getResourceAsStream("/relatorios/relatorio-usuario.jasper");
        var parametros = new HashMap<String, Object>();
        parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

        var jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }catch (Exception e){
        throw new RuntimeException("Não foi possivel emitir o relátorio");
        }
    }

    public byte[] gerarReportPrint(String relatorio, Map<String, Object> params, ServletContext servletContext) throws JRException, SQLException {
        try{
        Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        // Create the JasperPrint for the first report
        InputStream reportStream = getClass().getResourceAsStream("/relatorios/relatorio-usuario-param.jasper");
        var parametros = new HashMap<String, Object>();
        parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

        var jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }catch (Exception e){
        throw new RuntimeException("Não foi possivel emitir o relatorio");
        }
    }
}
