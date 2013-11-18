package net.conriot.economy;

import net.conriot.sona.mysql.IOCallback;
import net.conriot.sona.mysql.Result;

class EconomyManager implements IOCallback {
	public EconomyManager() {
		// TODO
	}
	
	public void payPlayer(String from, String to, double amount, TransactionCallback callback) {
		// Convert into an arbitrary payment call (player -> player)
		pay("", from, "", to, amount, callback);
	}
	
	public void givePlayer(String to, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (server -> player)
		pay("**", "", "", to, amount, callback);
	}
	
	public void takePlayer(String from, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (player -> server)
		pay("", from, "**", "", amount, callback);
	}
	
	public void pay(String fromPrefix, String from, String toPrefix, String to, double amount, TransactionCallback callback) {
		
	}

	@Override
	public void complete(boolean success, Object tag, Result result) {
		// TODO
	}
}
