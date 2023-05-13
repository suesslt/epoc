package com.jore.epoc.services;

import com.jore.epoc.dto.MarketDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public interface StaticDataService {
    void loadMarkets(@NotEmpty String xlsFileName);

    void loadSettings(@NotEmpty String xlsFileName);

    MarketDto saveMarket(@Valid MarketDto marketDto);
}
