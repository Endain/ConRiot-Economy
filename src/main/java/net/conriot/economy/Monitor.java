package net.conriot.economy;

import java.text.DecimalFormat;
import java.util.Date;

import net.conriot.sona.mysql.IOCallback;
import net.conriot.sona.mysql.MySQL;
import net.conriot.sona.mysql.Query;
import net.conriot.sona.mysql.Result;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class Monitor implements IOCallback {
	private Player caller;
	private String prefix;
	private String owner;
	// Specific transactions parameters
	private String target;
	
	public Monitor(Player caller, String prefix, String owner, String target) {
		this.caller = caller;
		this.prefix = prefix;
		this.owner = owner;
		this.target = target;
	}
	
	public void listSelf() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the 10 most recent transactions
			Query q = MySQL.makeQuery();
			q.setQuery("SELECT * FROM economy WHERE owner=?");
			q.add(this.prefix + this.owner);
			// Execute query to asynchronously
			MySQL.execute(this, "listSelf", q);
		}
	}
	
	public void listRecent() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the 10 most recent transactions
			Query q = MySQL.makeQuery();
			q.setQuery("SELECT * FROM economy_log WHERE from=? OR to=? ORDER BY time DESC LIMIT 10");
			q.add(this.prefix + this.owner);
			q.add(this.prefix + this.owner);
			// Execute query to asynchronously
			MySQL.execute(this, "listRecent", q);
		}
	}
	
	public void listTop() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the top ten balanced
			Query q = MySQL.makeQuery();
			q.setQuery("SELECT * FROM economy WHERE owner NOT LIKE '*%' ORDER BY balance DESC LIMIT 10");
			// Execute query to asynchronously
			MySQL.execute(this, "listTop", q);
		}
	}
	
	public void listSpecific() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the 10 most recent transactions
			Query q = MySQL.makeQuery();
			q.setQuery("SELECT * FROM economy_log WHERE (from=? AND to LIKE '%?%') OR (from LIKE '%?%' AND to=?) ORDER BY time DESC LIMIT 10");
			q.add(this.prefix + this.owner);
			q.add(target);
			q.add(target);
			q.add(this.prefix + this.owner);
			// Execute query to asynchronously
			MySQL.execute(this, "listSpecific", q);
		}
	}
	
	private void printSelf(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.caller.sendMessage(ChatColor.RED + "There is no account for '" + this.owner + "'!");
			} else {
				while(result.next()) {
					this.caller.sendMessage(ChatColor.GREEN + "Balance for '" + ChatColor.DARK_GREEN + (String)result.get(0) + ChatColor.GREEN + "': $" + ChatColor.DARK_GREEN + (double)result.get(1));
				}
			}
		}
	}
	
	private void printRecent(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.caller.sendMessage(ChatColor.RED + "There are no recorded transactions for '" + this.owner + "'!");
			} else {
				this.caller.sendMessage(ChatColor.YELLOW + "Recent transactions for '" + ChatColor.GOLD + this.owner + ChatColor.YELLOW +  "':");
				DecimalFormat df = new DecimalFormat("#.00");
				while(result.next()) {
					Date date = new Date((long)result.get(4));
					String entry = ChatColor.GREEN + "  [#" + ChatColor.DARK_GREEN + (int)result.get(0) +
						ChatColor.GREEN + " ::: $" + ChatColor.DARK_GREEN + df.format((double)result.get(3)) + ChatColor.GREEN + " from " +
						ChatColor.DARK_GREEN + (String)result.get(1) + ChatColor.GREEN + " to " + ChatColor.DARK_GREEN + (String)result.get(2) +
						ChatColor.GREEN + " at " + ChatColor.DARK_GREEN + date.toString() + ChatColor.GREEN + "]";
					this.caller.sendMessage(entry);
				}
			}
		}
	}
	
	private void printTop(Result result) {
		
	}
	
	private void printSpecific(Result result) {
		
	}

	@Override
	public void complete(boolean success, Object tag, Result result) {
		if(tag instanceof String) {
			String type = (String)tag;
			
			// Print based on the type of query
			switch(type) {
			case "listSelf":
				printSelf(result);
				break;
			case "listRecent":
				printRecent(result);
				break;
			case "listTop":
				printTop(result);
				break;
			case "listSpecific":
				printSpecific(result);
				break;
			}
		}
	}
}
