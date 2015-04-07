package com.market.trade.porcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class TradeMessageProcessorFlat {

	private static final String AMOUNT_SELL = "amountSell";
	private static final String CURRENCY_TO = "currencyTo";
	private static final String CURRENCY_FROM = "currencyFrom";
	private static final String TRADE_MESSAGE_OUT_TXT = "c:\\users\\administrator\\tradeMessageOut.txt";

	protected static synchronized void writeMessage(String message) {
		File file = getSharedFile();
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonMesssage = jsonParser.parse(message);
		JsonObject  jobject = jsonMesssage.getAsJsonObject();
		String currencyFrom = jobject.getAsJsonPrimitive(CURRENCY_FROM).getAsString();
	    StringBuffer keyBuffer = new StringBuffer();
	    keyBuffer.append(currencyFrom);
	    keyBuffer.append('-');
	    
	    String currencyTo = jobject.getAsJsonPrimitive(CURRENCY_TO).getAsString();
	    keyBuffer.append(currencyTo);
	    
	    String jsonKey = keyBuffer.toString();
	    String keyValue = jobject.getAsJsonPrimitive(AMOUNT_SELL).toString();

	    JsonParser jsonFileParser = new JsonParser();
	    JsonObject fileJsonObject;
	    try {
			JsonElement jsonFile = jsonFileParser.parse(new FileReader(file));
			if(file.exists() && file.length()>0){
				fileJsonObject = jsonFile.getAsJsonObject();
				if(fileJsonObject.has(jsonKey)){
					String valueJobject = fileJsonObject.getAsJsonPrimitive(jsonKey).toString();
					keyValue = String.valueOf(Double.parseDouble(keyValue) + Double.parseDouble(valueJobject));
				}
			}else{
				fileJsonObject = new JsonObject();
			}
			fileJsonObject.addProperty(jsonKey, Double.valueOf(keyValue));
			writeJson(fileJsonObject);
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static File getSharedFile() {
		File file = new File(TRADE_MESSAGE_OUT_TXT);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	private static void writeJson(JsonObject fileJsonObject) {
		try {
			FileWriter fileWriter = new FileWriter(TRADE_MESSAGE_OUT_TXT);
			fileWriter.write(fileJsonObject.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
