package net.conriot.sona.economy;

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
	// Check balance callback
	private TransactionCallback callback;
	
	public Monitor(Player caller, String prefix, String owner, String target) {
		this.caller = caller;
		this.prefix = prefix;
		this.owner = owner;
		this.target = target;
		this.callback = null; // Unused in this case
	}
	
	public Monitor(Player caller, TransactionCallback callback) {
		this.caller = caller;
		this.prefix = "";
		this.owner = caller.getName();
		this.target = caller.getName();
		this.callback = callback;
	}
	
	public void checkBalance() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the owners account balance
			Query q = MySQL.makeQuery();
			q.setQuery("SELECT * FROM economy WHERE owner=?");
			q.add(this.prefix + this.owner);
			// Execute query to asynchronously
			MySQL.execute(this, "checkBalance", q);
		}
	}
	
	public void listSelf() {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			// Create a query to get the owners account balance
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
			q.setQuery("SELECT * FROM economy_log WHERE economy_log.from=? OR economy_log.to=? ORDER BY time DESC LIMIT 10");
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
			q.setQuery("SELECT * FROM economy_log WHERE (economy_log.from=? AND economy_log.to LIKE ?) OR (economy_log.from LIKE ? AND economy_log.to=?) ORDER BY time DESC LIMIT 10");
			q.add(this.prefix + this.owner);
			q.add("%" + target + "%");
			q.add("%" + target + "%");
			q.add(this.prefix + this.owner);
			// Execute query to asynchronously
			MySQL.execute(this, "listSpecific", q);
		}
	}
	
	private void sendBalance(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.callback.complete(Status.SYSTEM_ERROR, null);
			} else {
				if(result.next()) {
					Transaction trans = new Transaction("", this.owner, "", this.owner, (double)result.get(0));
					this.callback.complete(Status.SUCCESS, trans);
				} else
					this.callback.complete(Status.SYSTEM_ERROR, null);
			}
		}
	}
	
	private void printSelf(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.caller.sendMessage(ChatColor.RED + "There is no account for '" + this.owner + "'!");
			} else {
				// Print the result
				DecimalFormat df = new DecimalFormat("#.00");
				if(result.next()) {
					this.caller.sendMessage(ChatColor.GREEN + "Balance for '" + ChatColor.DARK_GREEN + (String)result.get(0) + ChatColor.GREEN + "': $" + ChatColor.DARK_GREEN + df.format((double)result.get(1)));
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
					// Print the transaction id number
					if(this.caller.getName().equalsIgnoreCase((String)result.get(1)))
						this.caller.sendMessage(ChatColor.RED + " [ Transaction #" + ChatColor.DARK_RED + (int)result.get(0) + ChatColor.RED + " ]");
					else
						this.caller.sendMessage(ChatColor.GREEN + " [ Transaction #" + ChatColor.DARK_GREEN + (int)result.get(0) + ChatColor.GREEN + "]");
					// Print the amount/from/to
					String msg = ChatColor.DARK_GRAY + "   >  " + ChatColor.GRAY + "$" + ChatColor.GOLD + df.format((double)result.get(3)) + ChatColor.GRAY + " from " +
						ChatColor.GOLD + (String)result.get(1) + ChatColor.GRAY + " to " + ChatColor.GOLD + (String)result.get(2);
					this.caller.sendMessage(msg);
					// Print the date the transaction occurred
					Date date = new Date((long)result.get(4));
					this.caller.sendMessage(ChatColor.GRAY + "      @ " + ChatColor.YELLOW + date.toString());
				}
			}
		}
	}
	
	private void printTop(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.caller.sendMessage(ChatColor.RED + "There are no recorded accounts to list!");
			} else {
				this.caller.sendMessage(ChatColor.YELLOW + "Top 10 balances on the server:");
				DecimalFormat df = new DecimalFormat("#.00");
				int count = 1;
				while(result.next()) {
					String entry = ChatColor.GREEN + " " + count + ".  " + ChatColor.DARK_GREEN + (String)result.get(0) +
						ChatColor.GREEN + " with $" + ChatColor.DARK_GREEN + df.format((double)result.get(1));
					this.caller.sendMessage(entry);
					count++;
				}
			}
		}
	}
	
	private void printSpecific(Result result) {
		// Don't bother trying if our caller is offline or null
		if(this.caller != null && this.caller.isOnline()) {
			if(result == null) {
				this.caller.sendMessage(ChatColor.RED + "There are no recorded transactions between '" + this.owner + "' and '" + this.target + "'!");
			} else {
				this.caller.sendMessage(ChatColor.YELLOW + "Recent transactions between '" + ChatColor.GOLD + this.owner + ChatColor.YELLOW +  "' and '" + ChatColor.GOLD + this.target + ChatColor.YELLOW + "':");
				DecimalFormat df = new DecimalFormat("#.00");
				while(result.next()) {
					// Print the transaction id number
					if(this.caller.getName().equalsIgnoreCase((String)result.get(1)))
						this.caller.sendMessage(ChatColor.RED + " [ Transaction #" + ChatColor.DARK_RED + (int)result.get(0) + ChatColor.RED + " ]");
					else
						this.caller.sendMessage(ChatColor.GREEN + " [ Transaction #" + ChatColor.DARK_GREEN + (int)result.get(0) + ChatColor.GREEN + "]");
					// Print the amount/from/to
					String msg = ChatColor.DARK_GRAY + "   >  " + ChatColor.GRAY + "$" + ChatColor.GOLD + df.format((double)result.get(3)) + ChatColor.GRAY + " from " +
						ChatColor.GOLD + (String)result.get(1) + ChatColor.GRAY + " to " + ChatColor.GOLD + (String)result.get(2);
					this.caller.sendMessage(msg);
					// Print the date the transaction occurred
					Date date = new Date((long)result.get(4));
					this.caller.sendMessage(ChatColor.GRAY + "      @ " + ChatColor.YELLOW + date.toString());
				}
			}
		}
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
			case "checkBalance":
				sendBalance(result);
				break;
			}
		}
	}
}
