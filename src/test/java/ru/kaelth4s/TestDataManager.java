package ru.kaelth4s;

import ru.kaelth4s.dto.MarketTestData;
import ru.kaelth4s.dto.TopSellersTestData;
import ru.kaelth4s.util.JsonUtils;

public class TestDataManager {

    private static MarketTestData marketTestData;
    private static TopSellersTestData topSellersTestData;

    public TestDataManager() {}

    public static MarketTestData getMarketTestData() {
        if (marketTestData == null) marketTestData = JsonUtils.mapFromResources("test-data/market-test.json", MarketTestData.class);
        return marketTestData;
    }

    public static TopSellersTestData getTopSellersTestData() {
        if (topSellersTestData == null) topSellersTestData = JsonUtils.mapFromResources("test-data/top-sellers-test.json", TopSellersTestData.class);
        return topSellersTestData;
    }
}
