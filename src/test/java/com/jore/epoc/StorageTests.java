package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Storage;

class StorageTests {
    private static final YearMonth START_MONTH = YearMonth.of(2023, 1);
    private Storage storage = new Storage();
    private Money unitPrice = Money.of("CHF", 50);

    @BeforeEach
    public void setUp() {
        Company company = new Company();
        storage.setCompany(company);
        storage.setCapacity(1000);
        storage.setStorageStartMonth(START_MONTH);
    }

    @Test
    public void testAveragePrice() {
        Company company = new Company();
        company.addStorage(StorageBuilder.builder().company(company).capacity(150).storageStartMonth(START_MONTH).build());
        company.addStorage(StorageBuilder.builder().company(company).capacity(150).storageStartMonth(START_MONTH).build());
        Storage.distributeRawMaterialAccrossStorages(company.getStorages(), 100, START_MONTH, Money.of("CHF", 30));
        Storage.distributeRawMaterialAccrossStorages(company.getStorages(), 100, START_MONTH, Money.of("CHF", 80));
        Storage.distributeRawMaterialAccrossStorages(company.getStorages(), 100, START_MONTH, Money.of("CHF", 100));
        assertEquals(Money.of("CHF", 70), Storage.getAverageRawMaterialPrice(company.getStorages()));
        Storage.removeRawMaterialFromStorages(company.getStorages(), 180);
        assertEquals(120, Storage.getTotalStored(company.getStorages()));
        assertEquals(Money.of("CHF", 70), Storage.getAverageRawMaterialPrice(company.getStorages()));
    }

    @Test
    public void testDailyRemoval() {
        Company company = new Company();
        company.addStorage(StorageBuilder.builder().company(company).capacity(150).storageStartMonth(START_MONTH).build());
        company.addStorage(StorageBuilder.builder().company(company).capacity(150).storageStartMonth(START_MONTH).build());
        Storage.distributeRawMaterialAccrossStorages(company.getStorages(), 290, START_MONTH, Money.of("CHF", 10));
        Factory factory = FactoryBuilder.builder().company(company).productionStartMonth(START_MONTH).dailyCapacityPerProductionLine(8).productionLines(1).build();
        company.addFactory(factory);
        int produced = factory.produce(100, START_MONTH, 1.0d);
        assertEquals(176, produced);
        assertEquals(290, Storage.getTotalStored(company.getStorages()));
        assertEquals(114, Storage.getRawMaterialStored(company.getStorages()));
        assertEquals(176, Storage.getProductsStored(company.getStorages()));
    }

    @Test
    public void testMultipleStoragesAndDistributeProduct() {
        Company company = new Company();
        company.addStorage(StorageBuilder.builder().company(company).capacity(500).storageStartMonth(START_MONTH).build());
        company.addStorage(StorageBuilder.builder().company(company).capacity(500).storageStartMonth(START_MONTH).build());
        Storage.distributeProductAccrossStorages(company.getStorages(), 600, START_MONTH);
        assertEquals(600, Storage.getTotalStored(company.getStorages()));
        assertEquals(400, company.getStorages().stream().mapToInt(storage -> storage.getAvailableCapacity(START_MONTH)).sum());
    }

    @Test
    public void testMultipleStoragesAndDistributeRawMaterials() {
        Company company = new Company();
        company.addStorage(StorageBuilder.builder().company(company).capacity(500).storageStartMonth(START_MONTH).build());
        company.addStorage(StorageBuilder.builder().company(company).capacity(500).storageStartMonth(START_MONTH).build());
        Storage.distributeRawMaterialAccrossStorages(company.getStorages(), 700, START_MONTH, unitPrice);
        assertEquals(700, Storage.getTotalStored(company.getStorages()));
        assertEquals(300, company.getStorages().stream().mapToInt(storage -> storage.getAvailableCapacity(START_MONTH)).sum());
    }

    @Test
    public void testNumberStored() {
        assertEquals(300, storage.storeProducts(300, START_MONTH));
        assertEquals(300, storage.storeRawMaterials(300, START_MONTH, unitPrice));
        assertEquals(300, storage.storeProducts(300, START_MONTH));
        assertEquals(100, storage.storeRawMaterials(300, START_MONTH, unitPrice));
        assertEquals(600, storage.getStoredProducts());
        assertEquals(400, storage.getStoredRawMaterials());
    }

    @Test
    public void testStoreNotYetAvailable() {
        assertEquals(0, storage.getAvailableCapacity(YearMonth.of(2022, 12)));
    }

    @Test
    public void testStoreProducts() {
        assertEquals(1000, storage.getAvailableCapacity(START_MONTH));
        storage.storeProducts(500, START_MONTH);
        assertEquals(500, storage.getAvailableCapacity(START_MONTH));
        assertEquals(500, storage.getStoredProducts());
        assertEquals(0, storage.getStoredRawMaterials());
    }

    @Test
    public void testStoreRawMaterial() {
        assertEquals(1000, storage.getAvailableCapacity(START_MONTH));
        storage.storeRawMaterials(500, START_MONTH, unitPrice);
        assertEquals(500, storage.getAvailableCapacity(START_MONTH));
        assertEquals(500, storage.getStoredRawMaterials());
        assertEquals(0, storage.getStoredProducts());
    }
}
