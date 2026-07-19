package com.africanmission.controller;

import com.africanmission.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @GetMapping("/weather")
    public Map<String, Object> getWeather() {
        return marketDataService.getWeather();
    }

    @GetMapping("/prices")
    public List<Map<String, Object>> getPrices() {
        return marketDataService.getMarketPrices();
    }
}