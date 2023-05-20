package com.jore.epoc.services.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.settings.EpocSetting;
import com.jore.epoc.bo.settings.EpocSettings;
import com.jore.epoc.dto.EpocSettingDto;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.mapper.MarketMapper;
import com.jore.epoc.repositories.MarketRepository;
import com.jore.epoc.repositories.SettingRepository;
import com.jore.epoc.repositories.SettingsRepository;
import com.jore.epoc.services.StaticDataService;
import com.jore.excel.ExcelReader;
import com.jore.excel.ExcelWorkbook;
import com.jore.view.FieldModel;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Validated
@Service
public class StaticDataServiceImpl implements StaticDataService {
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    SettingsRepository settingsRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public void loadMarkets(String xlsFileName) {
        try {
            ExcelWorkbook workbook = new ExcelWorkbook(new ClassPathResource(xlsFileName).getInputStream());
            List<MarketDto> markets = new ExcelReader<MarketDto>(workbook, new FieldModel<>(MarketDto.class)).read();
            markets.forEach(market -> saveMarket(market));
            workbook.close();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    @Transactional
    public void loadSettings(String xlsFileName) {
        try {
            EpocSettings epocSettings = new EpocSettings();
            epocSettings.setTemplate(true);
            Resource resource = new ClassPathResource(xlsFileName);
            InputStream inputStream = resource.getInputStream();
            ExcelWorkbook workbook = new ExcelWorkbook(inputStream);
            List<EpocSettingDto> settings = new ExcelReader<EpocSettingDto>(workbook, new FieldModel<>(EpocSettingDto.class)).read();
            for (EpocSettingDto epocSettingDto : settings) {
                EpocSetting setting = new EpocSetting();
                setting.setDescription(epocSettingDto.getDescription());
                setting.setSettingFormat(epocSettingDto.getSettingFormat());
                setting.setSettingKey(epocSettingDto.getSettingKey());
                setting.setValueText(epocSettingDto.getValueText());
                epocSettings.addSetting(setting);
                log.debug(setting);
            }
            settingsRepository.save(epocSettings);
            workbook.close();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public MarketDto saveMarket(MarketDto marketDto) {
        Optional<Market> market = marketRepository.findByName(marketDto.getName());
        if (market.isPresent()) {
            MarketMapper.INSTANCE.updateMarketFromMarketDto(market.get(), marketDto);
        } else {
            market = Optional.of(MarketMapper.INSTANCE.marketDtoToMarket(marketDto));
        }
        marketRepository.save(market.get());
        log.debug(market);
        return marketDto;
    }
}
