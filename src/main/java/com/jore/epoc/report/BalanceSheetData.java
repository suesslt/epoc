package com.jore.epoc.report;

import lombok.Data;

@Data
public class BalanceSheetData {
    private String balanceSheetDate = "Today";
    private String companyName = "The Best Company";
    private String cashThis;
    private String cashPrev;
    private String receivableThis;
    private String receivablePrev;
    private String inventoryRawMaterialThis;
    private String inventoryRawMaterialPrev;
    private String inventoryProductsThis;
    private String inventoryProductsPrev;
    private String totalCurrentAssetsThis;
    private String totalCurrentAssetsPrev;
    private String totalFixedAssetsThis;
    private String totalFixedAssetsPrev;
    private String propertiesThis;
    private String propertiesPrev;
    private String longTermDebtThis;
    private String longTermDebtPrev;
    private String totalCurrentLiabilitiesThis;
    private String totalCurrentLiabilitiesPrev;
    private String totalLiabilitiesThis;
    private String totalLiabilitiesPrev;
    private String ownersEquityThis;
    private String ownersEquityPrev;
    private String totalLiabilitiesAndOwnersEquityThis;
    private String totalLiabilitiesAndOwnersEquityPrev;
    private String totalAssetsThis;
    private String totalAssetsPrev;
    private String thisPeriod;
    private String previousPeriod;
}
