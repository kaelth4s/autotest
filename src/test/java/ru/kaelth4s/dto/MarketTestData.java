package ru.kaelth4s.dto;

import java.util.List;

public record MarketTestData(List<Filter> filters, String search, int listingOrder) {}
