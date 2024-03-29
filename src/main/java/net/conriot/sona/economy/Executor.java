package net.conriot.sona.economy;

import net.conriot.sona.mysql.IOCallback;
import net.conriot.sona.mysql.MySQL;
import net.conriot.sona.mysql.Query;
import net.conriot.sona.mysql.Result;

class Executor implements IOCallback {
	private Transaction transaction;
	private TransactionCallback callback;
	private double newFromBal;
	private double newToBal;
	
	public Executor(Transaction transaction, TransactionCallback callback, double newFromBal, double newToBal) {
		this.transaction = transaction;
		this.callback = callback;
		this.newFromBal = newFromBal;
		this.newToBal = newToBal;
		
		// Execute this transaction
		execute();
	}
	
	private void execute() {
		// Determine if this is a Server -> Account, Account -> Server or Account -> Account transaction
		if(this.transaction.getFromPrefix().equals("**"))
			serverToAccount();
		else if(this.transaction.getToPrefix().equals("**"))
			accountToServer();
		else
			accountToAccount();
		
	}
	
	private void serverToAccount() {
		// Create a query to add money to the account
		Query q = MySQL.makeQuery();
		q.setQuery("UPDATE economy SET balance=? WHERE owner=?");
		q.add(this.newToBal);
		q.add(this.transaction.getToPrefix() + this.transaction.getTo());
		// Execute query asynchronously
		MySQL.execute(this, "execute", q);
	}
	
	private void accountToServer() {
		// Create a query to remove money to the account
		Query q = MySQL.makeQuery();
		q.setQuery("UPDATE economy SET balance=? WHERE owner=?");
		q.add(this.newFromBal);
		q.add(this.transaction.getFromPrefix() + this.transaction.getFrom());
		// Execute query asynchronously
		MySQL.execute(this, "execute", q);
	}
	
	private void accountToAccount() {
		// Create a query to remove money from one accounts
		Query qRemove = MySQL.makeQuery();
		qRemove.setQuery("UPDATE economy SET balance=? WHERE owner=?");
		qRemove.add(this.newFromBal);
		qRemove.add(this.transaction.getFromPrefix() + this.transaction.getFrom());
		// Execute query asynchronously
		MySQL.execute(this, "continue", qRemove);
		
		// Create a query to add money to the other account
		Query qAdd = MySQL.makeQuery();
		qAdd.setQuery("UPDATE economy SET balance=? WHERE owner=?");
		qAdd.add(this.newToBal);
		qAdd.add(this.transaction.getToPrefix() + this.transaction.getTo());
		// Execute query asynchronously
		MySQL.execute(this, "execute", qAdd);
	}
	
	private void log() {
		// Create a query to add money to the account
		Query q = MySQL.makeQuery();
		q.setQuery("INSERT INTO economy_log (economy_log.from,economy_log.to,economy_log.amount,economy_log.time) VALUES (?,?,?,?)");
		q.add(this.transaction.getFromPrefix() + this.transaction.getFrom());
		q.add(this.transaction.getToPrefix() + this.transaction.getTo());
		q.add(this.transaction.getAmount());
		q.add(System.currentTimeMillis());
		// Execute query asynchronously
		MySQL.execute(this, "log", q);
	}

	@Override
	public void complete(boolean success, Object tag, Result result) {
		// Perform a callback when executing the transaction is done
		if(tag instanceof String && ((String)tag).equals("execute")) {
			if(success) {
				this.callback.complete(Status.SUCCESS, this.transaction);
				// Log this transaction if it was successful
				log();
			} else
				this.callback.complete(Status.SYSTEM_ERROR, this.transaction);
		}
	}
}
