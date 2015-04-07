package com.market.trade.porcessor;

import javax.ejb.Local;

@Local
public interface SimpleTradeMessageClientLocal {
	public int sendMessage();
}
