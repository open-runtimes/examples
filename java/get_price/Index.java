import com.google.gson.Gson;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

final Gson gson = new Gson();

final Map<String, String> SYMBOLS = Map.of(
    "amazon", "AMZN",
    "bitcoin", "BTC-USD",
    "ethereum", "ETH-USD",
    "gold", "GC=F",
    "google", "GOOG",
    "silver", "SI=F"
);

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) {
    String payloadString = req.getPayload();
    Map<String, Object> payload = gson.fromJson(payloadString, Map.class);
    Map<String, Object> responseData = new HashMap<>();

    String type = payload.get("type").toString();
    String symbol = SYMBOLS.get(type);

    if (symbol == null) {
        responseData.put("success", false);
        responseData.put("message", "Type is not supported.");
        return res.json(responseData);
    }

    try {
        BigDecimal price = getPrice(symbol);
        responseData.put("success", true);
        responseData.put("price", price);

        return res.json(responseData);
    } catch (IOException ex) {
        responseData.put("success", false);
        responseData.put("message", ex.getMessage());
        return res.json(responseData);
    }
}

private BigDecimal getPrice(String symbol) throws IOException {
    Stock stock = YahooFinance.get(symbol);
    BigDecimal price = stock.getQuote().getPrice();
    return price;
}