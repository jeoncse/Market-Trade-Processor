package com.market.trade.porcessor;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Message-Driven Bean implementation class for: TradeMessageConsumer
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(
		propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(propertyName = "destination", propertyValue = "MyQueue")
})
public class TradeMessageConsumer implements MessageListener {
	@Resource
	private MessageDrivenContext mdc;
	private TradeMessageProcessor tradeMessageProcessor;
	/**
	 * Default constructor. 
	 */
	public TradeMessageConsumer() {
		if(tradeMessageProcessor == null){
			tradeMessageProcessor = new TradeMessageProcessor();
		}
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	@Override
	public void onMessage(Message message) {
		TextMessage msg = null;

		try {
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				System.out.println("MESSAGE BEAN: Message received: " +
						msg.getText());
				TradeMessageProcessorFlat.writeMessage(msg.getText());
			} else {
				System.out.println("Message of wrong type: " +
						message.getClass().getName());
			}
		} catch (JMSException e) {
			e.printStackTrace();
			mdc.setRollbackOnly();
		} catch (Throwable te) {
			te.printStackTrace();
		}
	}
}
