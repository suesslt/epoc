package com.jore.epoc;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

class JasperReportTests {
    @Test
    void test() throws JRException {
        JasperReport report = JasperCompileManager.compileReport("/Users/thomassussli/workspace/epoc/reports/BalanceSheet.jrxml");
        JRSaver.saveObject(report, "/Users/thomassussli/JaspersoftWorkspace/MyReports/JasperTest.jasper");
        BalanceSheetData testReportData = new BalanceSheetData();
        ArrayList<BalanceSheetData> list = new ArrayList<>();
        list.add(testReportData);
        JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(list);
        JasperPrint print = JasperFillManager.fillReport(report, null, source);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("/Users/thomassussli/JaspersoftWorkspace/MyReports/JasperTest.pdf"));
        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);
        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("thomas");
        exportConfig.setEncrypted(false);
        exportConfig.setAllowedPermissionsHint("PRINTING");
        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);
        exporter.exportReport();
    }
}
