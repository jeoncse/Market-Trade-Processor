package com.market.trade.socket.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class WSMarketTradeClient {

	private static CountDownLatch latch;
	
	private String message;
	 
    private Logger logger = Logger.getLogger(this.getClass().getName());
 
    public WSMarketTradeClient(String message) {
    	this.message = message;
	}
    
    @OnOpen
    public void onOpen(Session session) {
    	try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        latch.countDown();
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}