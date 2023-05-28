package com.jore.epoc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.report.BalanceSheetData;
import com.jore.epoc.report.BalanceSheetReport;

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
    public void testBalanceSheetData() {
        Company company = SimulationBuilder.builder().build();
        company.setId(1385l);
        CompanySimulationStep companySimulationStep = company.getSimulation().getActiveSimulationStep().get().getCompanySimulationStepFor(company);
        companySimulationStep.finish();
        BalanceSheetReport report = new BalanceSheetReport();
        report.setSource(company, YearMonth.of(2020, 2), YearMonth.of(2020, 1));
        report.store("BalanceSheet", company.getId().toString(), LocalDate.of(2020, 1, 31).toString());
    }

    @Test
    public void testCompilation() throws JRException {
        JasperReport report = JasperCompileManager.compileReport("/Users/thomassussli/workspace/epoc/reports/BalanceSheet.jrxml");
        JRSaver.saveObject(report, "/Users/thomassussli/workspace/epoc/reports/BalanceSheet.jasper");
        BalanceSheetData testReportData = new BalanceSheetData();
        ArrayList<BalanceSheetData> list = new ArrayList<>();
        list.add(testReportData);
        JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(list);
        JasperPrint print = JasperFillManager.fillReport(report, null, source);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("/Users/thomassussli/workspace/epoc/reports/BalanceSheet.pdf"));
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
