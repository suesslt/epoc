package com.jore.epoc.services;

public interface StaticDataService {
    Object getSetting(String key);

    void loadEpocSettings(String xlsFileName);

    void loadMarkets(String xlsFileName);
}
