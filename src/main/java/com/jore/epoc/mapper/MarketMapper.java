package com.jore.epoc.mapper;

import com.jore.epoc.bo.Market;
import com.jore.epoc.dto.MarketDto;

public interface MarketMapper {
    public MarketMapper INSTANCE = new MarketMapper() {
        @Override
        public Market marketDtoToMarket(MarketDto marketDto) {
            Market result = new Market();
            result.setId(marketDto.getId());
            updateMarketFromMarketDto(result, marketDto);
            return result;
        }

        @Override
        public void updateMarketFromMarketDto(Market market, MarketDto marketDto) {
            market.setAge65olderFemale(marketDto.getAge65olderFemale());
            market.setAge65olderMale(marketDto.getAge65olderMale());
            market.setAgeTo14Female(marketDto.getAgeTo14Female());
            market.setAgeTo14Male(marketDto.getAgeTo14Male());
            market.setAgeTo24Female(marketDto.getAgeTo24Female());
            market.setAgeTo24Male(marketDto.getAgeTo24Male());
            market.setAgeTo54Female(marketDto.getAgeTo54Female());
            market.setAgeTo54Male(marketDto.getAgeTo54Male());
            market.setAgeTo64Female(marketDto.getAgeTo64Female());
            market.setAgeTo64Male(marketDto.getAgeTo64Male());
            market.setLaborForce(marketDto.getLaborForce());
            market.setGdp(marketDto.getGdp());
            market.setGdpGrowth(marketDto.getGdpGrowth());
            market.setGdpPpp(marketDto.getGdpPpp());
            market.setLifeExpectancy(marketDto.getLifeExpectancy());
            market.setName(marketDto.getName());
            market.setProductivity(marketDto.getProductivity());
            market.setUnemployment(marketDto.getUnemployment());
        }
    };

    public Market marketDtoToMarket(MarketDto marketDto);

    public void updateMarketFromMarketDto(Market market, MarketDto marketDto);
}
