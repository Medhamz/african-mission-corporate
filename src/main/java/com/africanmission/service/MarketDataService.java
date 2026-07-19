package com.africanmission.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MarketDataService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Value("${weather.api.city:Bamako}")
    private String city;

    @Value("${weather.api.units:metric}")
    private String units;

    @Value("${market.api.key}")
    private String marketApiKey;

    @Value("${market.api.symbols:COTTON,LUMBER,STEEL}")
    private List<String> symbols;

    // ============================================
    // MÉTÉO
    // ============================================

    @Cacheable(value = "weather", unless = "#result == null")
    public Map<String, Object> getWeather() {
        try {
            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&appid=%s",
                    city, units, weatherApiKey
            );
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Fallback en cas d'erreur
        return getSimulatedWeather();
    }

    private Map<String, Object> getSimulatedWeather() {
        Map<String, Object> weather = new LinkedHashMap<>();
        Map<String, Object> main = new LinkedHashMap<>();
        main.put("temp", 30 + Math.random() * 10);
        main.put("humidity", 40 + Math.random() * 30);

        List<Map<String, String>> weatherList = new ArrayList<>();
        Map<String, String> w = new LinkedHashMap<>();
        w.put("description", "Ensoleillé");
        w.put("icon", "01d");
        weatherList.add(w);

        weather.put("main", main);
        weather.put("weather", weatherList);
        weather.put("name", city);
        weather.put("_simulated", true);
        return weather;
    }

    // ============================================
    // MARCHÉS (commodoties)
    // ============================================

    @Cacheable(value = "marketData", unless = "#result == null")
    public List<Map<String, Object>> getMarketPrices() {
        List<Map<String, Object>> results = new ArrayList<>();

        for (String symbol : symbols) {
            try {
                String url = String.format(
                        "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                        symbol, marketApiKey
                );
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response != null) {
                    Map<String, Object> quote = (Map<String, Object>) response.get("Global Quote");
                    if (quote != null) {
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("symbol", symbol);
                        entry.put("price", Double.parseDouble(quote.get("05. price").toString()));
                        entry.put("change", Double.parseDouble(quote.get("09. change").toString()));
                        entry.put("changePercent", quote.get("10. change percent").toString());
                        results.add(entry);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Si aucun résultat, fallback simulé
        if (results.isEmpty()) {
            return getSimulatedMarketPrices();
        }
        return results;
    }

    private List<Map<String, Object>> getSimulatedMarketPrices() {
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Double> basePrices = Map.of(
                "COTTON", 82.5,
                "LUMBER", 450.0,
                "STEEL", 680.0
        );
        for (String symbol : symbols) {
            Map<String, Object> entry = new LinkedHashMap<>();
            double base = basePrices.getOrDefault(symbol, 100.0);
            double variation = (Math.random() - 0.5) * 10;
            double price = base + variation;
            entry.put("symbol", symbol);
            entry.put("price", Math.round(price * 100.0) / 100.0);
            entry.put("change", Math.round(variation * 100.0) / 100.0);
            entry.put("changePercent", String.format("%.2f%%", (variation / base) * 100));
            results.add(entry);
        }
        return results;
    }
}