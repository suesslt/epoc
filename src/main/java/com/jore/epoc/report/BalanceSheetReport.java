package com.jore.epoc.report;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import com.jore.datatypes.formatter.MoneyDecimalDigits;
import com.jore.datatypes.formatter.MoneyFormatter;
import com.jore.datatypes.money.Money;
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

    public void setSource(Company company, YearMonth thisMonth, YearMonth previousMonth) {
        LocalDate thisDate = thisMonth.atEndOfMonth();
        LocalDate previousDate = previousMonth.atEndOfMonth();
        FinancialAccounting accounting = company.getAccounting();
        data.setBalanceSheetDate(thisDate.toString());
        data.setCashThis(formatMoney(accounting.getCash(thisDate)));
        data.setCashPrev(formatMoney(accounting.getCash(previousDate)));
        data.setCompanyName(company.getName());
        data.setInventoryProductsThis(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, thisDate)));
        data.setInventoryProductsPrev(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, previousDate)));
        data.setInventoryRawMaterialThis(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, thisDate)));
        data.setInventoryRawMaterialPrev(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.PRODUCTS, previousDate)));
        data.setLongTermDebtThis(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.LONG_TERM_DEBT, thisDate)));
        data.setLongTermDebtPrev(formatMoney(accounting.getBalanceForAccount(FinancialAccounting.LONG_TERM_DEBT, previousDate)));
        data.setOwnersEquityThis(formatMoney(accounting.getOwnersCapital(thisDate)));
        data.setOwnersEquityPrev(formatMoney(accounting.getOwnersCapital(previousDate)));
        data.setPropertiesThis(formatMoney(accounting.getRealEstateBalance(thisDate)));
        data.setPropertiesPrev(formatMoney(accounting.getRealEstateBalance(previousDate)));
        data.setReceivableThis(formatMoney(accounting.getReceivables(thisDate)));
        data.setReceivablePrev(formatMoney(accounting.getReceivables(previousDate)));
        data.setTotalFixedAssetsThis(formatMoney(accounting.getTotalFixedAssets(thisDate)));
        data.setTotalFixedAssetsPrev(formatMoney(accounting.getTotalFixedAssets(previousDate)));
        data.setTotalCurrentAssetsThis(formatMoney(accounting.getTotalCurrentAssets(thisDate)));
        data.setTotalCurrentAssetsPrev(formatMoney(accounting.getTotalCurrentAssets(previousDate)));
        data.setTotalCurrentLiabilitiesThis(formatMoney(accounting.getTotalCurrentLiabilities(thisDate)));
        data.setTotalCurrentLiabilitiesPrev(formatMoney(accounting.getTotalCurrentLiabilities(previousDate)));
        data.setTotalLiabilitiesAndOwnersEquityThis(formatMoney(accounting.getTotalLiabilitiesAndOwnersEquity(thisDate)));
        data.setTotalLiabilitiesAndOwnersEquityPrev(formatMoney(accounting.getTotalLiabilitiesAndOwnersEquity(previousDate)));
        data.setTotalLiabilitiesThis(formatMoney(accounting.getTotalLiabilities(thisDate)));
        data.setTotalLiabilitiesPrev(formatMoney(accounting.getTotalLiabilities(previousDate)));
        data.setTotalAssetsThis(formatMoney(accounting.getTotalAssets(thisDate)));
        data.setTotalAssetsPrev(formatMoney(accounting.getTotalAssets(previousDate)));
        data.setThisPeriod(thisMonth.toString());
        data.setPreviousPeriod(previousMonth.toString());
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

    private String formatMoney(Money money) {
        MoneyFormatter formatter = new MoneyFormatter(MoneyDecimalDigits.NO_DECIMAL_DIGITS);
        return formatter.format(money);
    }
}
