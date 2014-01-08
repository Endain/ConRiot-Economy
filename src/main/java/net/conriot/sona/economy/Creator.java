package net.conriot.sona.economy;

import org.bukkit.Bukkit;

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
		
		// First check if the account exists
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
		MySQL.execute(this, "create:" + this.prefix + ":" + this.owner + ":" + this.balance, q);
	}
	
	@Override
	public void complete(boolean success, Object tag, Result result) {
		if(success) {
			if(tag instanceof String && ((String)tag).equals("check")) {
				// If the account did not exist then create it
				if(!result.next())
					create();
			} else if(tag instanceof String && ((String)tag).startsWith("create")) {
				String[] split = ((String)tag).split(":");
				Bukkit.getLogger().info("Created economy account '" + split[1] + split[2] + " with starting balance of $" + Economy.format(Double.parseDouble(split[3])));
			}
		}
	}
}
