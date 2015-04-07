package com.market.trade.porcessor;

import javax.ejb.Remote;

@Remote
public interface SimpleTradeMessageClientRemote {
	public int sendMessage();

}
