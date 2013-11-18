package net.conriot.economy;

import net.conriot.sona.mysql.IOCallback;
import net.conriot.sona.mysql.MySQL;
import net.conriot.sona.mysql.Query;
import net.conriot.sona.mysql.Result;

class Creator implements IOCallback {
	private String prefix;
	private String owner;
	private double balance;
	
	public Creator(String prefix, String owner, double balance) {
		this.prefix = prefix;
		this.owner = owner;
		this.balance = balance;
		
		check();
	}
	
	private void check() {
		// Create a query to get the accounts being used
		Query q = MySQL.makeQuery();
		q.setQuery("SELECT * FROM economy WHERE owner=?");
		q.add(this.prefix + this.owner);
		// Execute query to asynchronously check for economy data
		MySQL.execute(this, "check", q);
	}
	
	private void create() {
		// Create a query to get the accounts being used
		Query q = MySQL.makeQuery();
		q.setQuery("INSERT INTO economy (owner,balance) VALUEs (?,?)");
		q.add(this.prefix + this.owner);
		q.add(this.balance);
		// Execute query to asynchronously create economy data
		MySQL.execute(this, null, q);
	}
	
	@Override
	public void complete(boolean success, Object tag, Result result) {
		if(tag instanceof String && ((String)tag).equals("check")) {
			if(result == null)
				create();
		}
	}
}
