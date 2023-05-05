package com.jore.epoc.services;

public interface StaticDataService {
    Object getSetting(String key);

    void loadSettings(String xlsFileName);

    void loadMarkets(String xlsFileName);
}
