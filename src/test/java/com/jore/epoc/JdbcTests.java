package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Market;
import com.jore.epoc.repositories.MarketRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class JdbcTests {
    @Autowired
    MarketRepository marketRepository;

    @Test
    @Transactional
    void test() {
        marketRepository.save(MarketBuilder.builder().name("Europe").costToEnterMarket(Money.of("CHF", 1000000)).build());
        Iterable<Market> findAll = marketRepository.findAll();
        assertEquals(1, ((Collection<?>) findAll).size());
        Market market = findAll.iterator().next();
        assertEquals(1, market.getId());
    }
}
