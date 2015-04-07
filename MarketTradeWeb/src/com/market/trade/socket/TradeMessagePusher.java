package com.market.trade.socket;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint("/connect2WS")
public class TradeMessagePusher {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	List<List<String>> content = new ArrayList<List<String>>();

	static ScheduledExecutorService timer = Executors
			.newSingleThreadScheduledExecutor();

	private static Set<Session> tradeSocketSessions;

	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static JsonObject globalJsonObject;

	@OnOpen
	public void showTime(Session session) {
		logger.info("Connected to Trade Server with session id "
				+ session.getId());
		sendTimeToAll(session);
	}

	private void sendTimeToAll(Session session) {
		tradeSocketSessions = session.getOpenSessions();
		for (Session sess : tradeSocketSessions) {
			try {
				StringBuilder sb = new StringBuilder();
				// set table header
				sb.append("\t<table align = center>\n");
				sb.append("\t\t<th>" + "Report - Market Trade");
				sb.append("\t</th>\n");

				// set column header
				sb.append("\t<tr>\n");
				sb.append("\t\t<td>" + "Xchange" + "</td>\n");
				sb.append("\t\t<td>" + "Volume" + "</td>\n");
				sb.append("\t</tr>\n");

				
				if (populateList() && content != null && content.size() > 0) {
					for (List<String> row : content) {
						sb.append("\t<tr>\n");
						for (String cell : row) {
							sb.append("\t\t<td>" + cell + "</td>\n");
						}
						sb.append("\t</tr>\n");
					}
				}
				sb.append("\t<tr>\n");
				sb.append("\t\t<td>" + "Last update at  "
						+ LocalTime.now().format(timeFormatter) + "</td>\n");
				sb.append("\t</tr>\n");
				sb.append("\t</table>\n");
				sess.getBasicRemote().sendText(sb.toString());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
	}

	@OnMessage
	public String onMessage(String message, Session session) {
		logger.info("message :" + message);
		if (message == null) {
			return null;
		}
		if(modifyJsonObject(message)){
			sendTimeToAll(session);
		}
		return message;
	}

	private synchronized boolean modifyJsonObject(String message) {
		if (globalJsonObject == null) {
			globalJsonObject = new JsonObject();
		}
		JsonParser jsonFileParser = new JsonParser();
		JsonObject fileJsonObject;
		JsonElement jsonFile = jsonFileParser.parse(message);
		fileJsonObject = jsonFile.getAsJsonObject();
		String keyValue = fileJsonObject.get("value").getAsString();
		String key = fileJsonObject.get("key").getAsString();

		if (globalJsonObject.has(key)) {
			String valueJobject = globalJsonObject.getAsJsonPrimitive(key)
					.toString();
			keyValue = String.valueOf(Double.parseDouble(keyValue)
					+ Double.parseDouble(valueJobject));
		}

		globalJsonObject.addProperty(key, Double.parseDouble(keyValue));

		if (populateList()) {
			return true;
		}
		return false;
	}

	private boolean populateList() {
		try {
			Iterator<Entry<String, JsonElement>> currIterator = globalJsonObject.entrySet().iterator();
			content = new ArrayList<List<String>>();
			while (currIterator.hasNext()) {
				Entry<String, JsonElement> currencyTrade = currIterator.next();
				List<String> currencyList = new ArrayList<String>();
				currencyList.add(currencyTrade.getKey());
				currencyList.add(currencyTrade.getValue().getAsString());
				content.add(currencyList);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
	}
}
