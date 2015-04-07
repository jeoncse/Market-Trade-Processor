package com.market.trade.socket;

import java.io.File;
import java.io.FileReader;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
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
	private static final String TRADE_MESSAGE_OUT_TXT = "c:\\users\\administrator\\tradeMessageOut.txt";

	static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	private static Set<Session> tradeSocketSessions;

	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@OnOpen
	public void showTime(Session session) {
		logger.info("Connected to Trade Server with session id " + session.getId());
		tradeSocketSessions = session.getOpenSessions();

		// push trade messages every second to all web socket clients
		if (tradeSocketSessions.size() == 1) {
			timer.scheduleAtFixedRate(() -> sendTimeToAll(session), 0, 1,
					TimeUnit.SECONDS);
		}
	}

	private void sendTimeToAll(Session session) {
		tradeSocketSessions = session.getOpenSessions();
		if(!populateList() && (content == null || content.size() == 0)){
			logger.info("Graphical content generation failed");
			return;
		}
		for (Session sess : tradeSocketSessions) {
			try {
				StringBuilder sb = new StringBuilder();
				//set table header
				sb.append("\t<table>\n");
				sb.append("\t\t<th>" + "Report - Market Trade");
				sb.append("\t</th>\n");
				
				//set column header
				sb.append("\t<tr>\n");
				sb.append("\t\t<td>" + "Xcahnge" + "</td>\n");
				sb.append("\t\t<td>" + "Volume" + "</td>\n");
				sb.append("\t</tr>\n");

				for (List<String> row : content) {
					sb.append("\t<tr>\n");
					for (String cell : row) {
						sb.append("\t\t<td>" + cell + "</td>\n");
					}
					sb.append("\t</tr>\n");
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

	private boolean populateList() {
		File file = new File(TRADE_MESSAGE_OUT_TXT);
		if (file.exists()) {
			try {
				JsonParser jsonFileParser = new JsonParser();
				JsonElement jsonFile = jsonFileParser
						.parse(new FileReader(file));
				JsonObject jObj = jsonFile.getAsJsonObject();
				Iterator<Entry<String, JsonElement>> currIterator = jObj
						.entrySet().iterator();
				content = new ArrayList<List<String>>();
				while(currIterator.hasNext()) {
					Entry<String, JsonElement> currencyTrade = currIterator
							.next();
					List<String> currencyList = new ArrayList<String>();
					currencyList.add(currencyTrade.getKey());
					currencyList.add(currencyTrade.getValue().getAsString());
					content.add(currencyList);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
	}
}
