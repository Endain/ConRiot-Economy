package net.conriot.sona.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends JavaPlugin {
	private EconomyManager economy;
	
	@Override
	public void onEnable() {
		// Register the economy managed which will handle all transaction processing
		this.economy = new EconomyManager(this);
		// Register all basic economy commands for this manager
		Commands c = new Commands(this.economy);
		getCommand("pay").setExecutor(c);
		getCommand("bal").setExecutor(c);
		getCommand("baltop").setExecutor(c);
		getCommand("eco").setExecutor(c);
	}
	
	@Override
	public void onDisable() {
		// Nothing to do here
	}
	
	public void payPlayer(String from, String to, double amount, TransactionCallback callback) {
		// Convert into an arbitrary payment call (player -> player)
		this.economy.pay("", from, "", to, amount, callback);
	}
	
	public void givePlayer(String to, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (server -> player)
		this.economy.pay("**", "", "", to, amount, callback);
	}
	
	public void takePlayer(String from, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (player -> server)
		this.economy.pay("", from, "**", "", amount, callback);
	}
	
	public void pay(String fromPrefix, String from, String toPrefix, String to, double amount, TransactionCallback callback) {
		// Create a new Transaction an pass it to a Validator
		new Validator(new Transaction(fromPrefix, from, toPrefix, to, amount), callback);
	}
	
	public void getBalance(Player caller, String prefix, String owner, String target) {
		// Monitor the calling player's balance
		this.economy.monitor(caller, prefix, owner, target, "balance");
	}
	
	public void getRecent(Player caller, String prefix, String owner, String target) {
		// Monitor the calling players recent transactions
		this.economy.monitor(caller, prefix, owner, target, "balance");
	}

	public void getTop(Player caller, String prefix, String owner, String target) {
		// Monitor the top account balances
		this.economy.monitor(caller, prefix, owner, target, "balance");
	}

	public void getSpecific(Player caller, String prefix, String owner, String target) {
		// Monitor a set of transactions involving the given owner and target
		this.economy.monitor(caller, prefix, owner, target, "balance");
	}
	
	public void create(String prefix, String owner, double amount) {
		// Attempt to create an account for the given owner with the given starting amount
		this.economy.create(prefix, owner, amount);
	}
}
