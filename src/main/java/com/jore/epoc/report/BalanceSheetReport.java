package com.jore.epoc.report;

import java.time.LocalDate;
import java.util.ArrayList;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Log4j2
public class BalanceSheetReport {
    private static final String PDF = ".pdf";
    private final String UNDERSCORE = "_";
    BalanceSheetData data = new BalanceSheetData();
    private String path = "/Users/thomassussli/workspace/epoc/reports/";

    public BalanceSheetData getData() {
        return data;
    }

    public void setSource(Company company, LocalDate thisDate, LocalDate previousDate) {
        FinancialAccounting accounting = company.getAccounting();
        data.setBalanceSheetDate(thisDate.toString());
        data.setCashThis(accounting.getCash(thisDate).getFormattedAmount());
        data.setCashPrev(accounting.getCash(previousDate).getFormattedAmount());
        data.setCompanyName(company.getName());
        data.setInventoryProductsThis(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, thisDate).getFormattedAmount());
        data.setInventoryProductsPrev(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, previousDate).getFormattedAmount());
        data.setInventoryRawMaterialThis(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, thisDate).getFormattedAmount());
        data.setInventoryRawMaterialPrev(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, previousDate).getFormattedAmount());
        data.setLongTermDebtThis(accounting.getBalanceForAccount(FinancialAccounting.LONG_TERM_DEBT, thisDate).getFormattedAmount());
        data.setLongTermDebtPrev(accounting.getBalanceForAccount(FinancialAccounting.LONG_TERM_DEBT, previousDate).getFormattedAmount());
        data.setOwnersEquityThis(accounting.getOwnersCapital(thisDate).getFormattedAmount());
        data.setOwnersEquityPrev(accounting.getOwnersCapital(previousDate).getFormattedAmount());
        data.setPropertiesThis(accounting.getRealEstateBalance(thisDate).getFormattedAmount());
        data.setPropertiesPrev(accounting.getRealEstateBalance(previousDate).getFormattedAmount());
        data.setReceivableThis("");
        data.setReceivablePrev("");
        data.setTotalFixedAssetsThis(accounting.getTotalFixedAssets(thisDate).getFormattedAmount());
        data.setTotalFixedAssetsPrev(accounting.getTotalFixedAssets(previousDate).getFormattedAmount());
        data.setTotalCurrentAssetsThis(accounting.getTotalCurrentAssets(thisDate).getFormattedAmount());
        data.setTotalCurrentAssetsPrev(accounting.getTotalCurrentAssets(previousDate).getFormattedAmount());
        data.setTotalCurrentLiabilitiesThis(accounting.getTotalCurrentLiabilities(thisDate).getFormattedAmount());
        data.setTotalCurrentLiabilitiesPrev(accounting.getTotalCurrentLiabilities(previousDate).getFormattedAmount());
        data.setTotalLiabilitiesAndOwnersEquityThis(accounting.getTotalLiabilitiesAndOwnersEquity(thisDate).getFormattedAmount());
        data.setTotalLiabilitiesAndOwnersEquityPrev(accounting.getTotalLiabilitiesAndOwnersEquity(previousDate).getFormattedAmount());
        data.setTotalLiabilitiesThis(accounting.getTotalLiabilities(thisDate).getFormattedAmount());
        data.setTotalLiabilitiesPrev(accounting.getTotalLiabilities(previousDate).getFormattedAmount());
        data.setTotalAssetsPrev(accounting.getTotalAssets(thisDate).getFormattedAmount());
        data.setTotalAssetsPrev(accounting.getTotalAssets(previousDate).getFormattedAmount());
    }

    public void store(String reportName, String companyIdString, String dateString) {
        try {
            JasperReport report = JasperCompileManager.compileReport(path + "BalanceSheet.jrxml");
            ArrayList<BalanceSheetData> list = new ArrayList<>();
            list.add(data);
            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(report, null, source);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path + reportName + UNDERSCORE + companyIdString + UNDERSCORE + dateString + PDF));
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
        } catch (JRException e) {
            log.error(e);
        }
    }
}
