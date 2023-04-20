package com.jore.epoc.services;

public interface StaticDataService {
    Object getSetting(String key);

    void loadMarkets(String xlsFileName);

    void loadSettings(String xlsFileName);
}
