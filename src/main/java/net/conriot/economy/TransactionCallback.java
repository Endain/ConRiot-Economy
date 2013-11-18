package net.conriot.economy;

public interface TransactionCallback {
	// Define a callback method that notes success and passes back transaction description object
	public void complete(Status result, Transaction transaction);
}
