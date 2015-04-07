package com.market.trade.porcessor;

import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.market.trade.socket.client.WSMarketTradeClient;

@ClientEndpoint
public class TradeMessageProcessorFlat {

	private static final String AMOUNT_SELL = "amountSell";
	private static final String CURRENCY_TO = "currencyTo";
	private static final String CURRENCY_FROM = "currencyFrom";

	protected static synchronized void processMessage(String message) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonMesssage = jsonParser.parse(message);
		JsonObject jobject = jsonMesssage.getAsJsonObject();
		String currencyFrom = jobject.getAsJsonPrimitive(CURRENCY_FROM).getAsString();
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(currencyFrom);
		keyBuffer.append('-');

		String currencyTo = jobject.getAsJsonPrimitive(CURRENCY_TO).getAsString();
		keyBuffer.append(currencyTo);

		String jsonKey = keyBuffer.toString();
		String keyValue = jobject.getAsJsonPrimitive(AMOUNT_SELL).toString();
		JsonObject fileJsonObject = new JsonObject();
		
		fileJsonObject.addProperty("key", jsonKey);
		fileJsonObject.addProperty("value", Double.valueOf(keyValue));
		connect2Socket(fileJsonObject.toString());
	}

	private static void connect2Socket(String message) {
		ClientManager client = ClientManager.createClient();
		try {
			client.connectToServer(new WSMarketTradeClient(message), new URI(
					"ws://localhost:8080/MarketTradeWeb/connect2WS"));
			// latch.await();
		} catch (DeploymentException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
