package com.jore.epoc.services.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jore.epoc.bo.Market;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.mapper.MarketMapper;
import com.jore.epoc.repositories.MarketRepository;
import com.jore.epoc.services.StaticDataService;
import com.jore.excel.ExcelReader;
import com.jore.excel.ExcelWorkbook;
import com.jore.view.FieldModel;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StaticDataServiceImpl implements StaticDataService {
    @Autowired
    MarketRepository marketRepository;

    @Override
    public void loadMarkets(String xlsFileName) {
        readExcel(xlsFileName);
    }

    private void readExcel(String fileName) {
        try {
            Resource resource = new ClassPathResource(fileName);
            InputStream inputStream = resource.getInputStream();
            ExcelWorkbook workbook = new ExcelWorkbook(inputStream);
            List<MarketDto> markets = new ExcelReader<MarketDto>(workbook, new FieldModel<>(MarketDto.class)).read();
            markets.forEach(market -> updateMarketByName(market));
            workbook.close();
            log.info(workbook);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private MarketDto updateMarketByName(MarketDto marketDto) {
        Optional<Market> market = marketRepository.findByName(marketDto.getName());
        if (market.isPresent()) {
            MarketMapper.INSTANCE.updateMarketFromMarketDto(market.get(), marketDto);
        } else {
            market = Optional.of(MarketMapper.INSTANCE.marketDtoToMarket(marketDto));
        }
        marketRepository.save(market.get());
        log.info(market);
        return marketDto;
    }
}
