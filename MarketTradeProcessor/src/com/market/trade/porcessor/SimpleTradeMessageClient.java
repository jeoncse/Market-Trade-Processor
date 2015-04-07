package com.market.trade.porcessor;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.google.gson.JsonObject;

@Stateless
public class SimpleTradeMessageClient implements SimpleTradeMessageClientRemote, SimpleTradeMessageClientLocal{

	private static final int NUM_MSGS = 5;

	@Resource(mappedName="jms/DurableConnectionFactory")
	private static ConnectionFactory connectionFactory;

	@Resource(mappedName="MyQueue")
	private static Queue queue;

	@Override
	public int sendMessage() {
		int countMsg = 0;
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e3) {
			e3.printStackTrace();
		}
		Session session = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e4) {
			e4.printStackTrace();
		}
		MessageProducer messageProducer = null;
		try {
			messageProducer = session.createProducer(queue);
		} catch (JMSException e3) {
			e3.printStackTrace();
		}

		TextMessage message = null;
		try {
			message = session.createTextMessage();
		} catch (JMSException e2) {
			e2.printStackTrace();
		}

		for (int i = 0; i < NUM_MSGS; i++) {
			try {
				JsonObject jsonMessage = new JsonObject();
				jsonMessage.addProperty("userId", "134256");
				jsonMessage.addProperty("currencyFrom", "EUR");
				jsonMessage.addProperty("currencyTo", "GBP");
				jsonMessage.addProperty("amountSell", 1000);
			
				
//				{"userId": "134256", "currencyFrom": "EUR", "currencyTo": "GBP", "amountSell": 1000,
//               	 "amountBuy": 747.10, "rate": 0.7471, "timePlaced" : "24JAN15 10:27:44",
//               	 "originatingCountry" : "FR"}
				message.setText(jsonMessage.toString());
			} catch (JMSException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Sending message: " + message.getText());
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
			try {
				messageProducer.send(message);
				countMsg++;
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		return countMsg;
	}

}
