package net.conriot.sona.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends JavaPlugin {
	private static EconomyManager economy;
	
	@Override
	public void onEnable() {
		// Register the economy managed which will handle all transaction processing
		economy = new EconomyManager(this);
		// Register all basic economy commands for this manager
		Commands c = new Commands(economy);
		getCommand("pay").setExecutor(c);
		getCommand("bal").setExecutor(c);
		getCommand("baltop").setExecutor(c);
		getCommand("eco").setExecutor(c);
	}
	
	@Override
	public void onDisable() {
		// Nothing to do here
	}
	
	public static void payPlayer(String from, String to, double amount, TransactionCallback callback) {
		// Convert into an arbitrary payment call (player -> player)
		economy.pay("", from, "", to, amount, callback);
	}
	
	public static void givePlayer(String to, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (server -> player)
		economy.pay("**", "", "", to, amount, callback);
	}
	
	public static void takePlayer(String from, double amount, TransactionCallback callback) {
		// Convert to arbitrary payment call (player -> server)
		economy.pay("", from, "**", "", amount, callback);
	}
	
	public static void pay(String fromPrefix, String from, String toPrefix, String to, double amount, TransactionCallback callback) {
		// Create a new Transaction an pass it to a Validator
		new Validator(new Transaction(fromPrefix, from, toPrefix, to, amount), callback);
	}
	
	public static void getBalance(Player caller, String prefix, String owner, String target) {
		// Monitor the calling player's balance
		economy.monitor(caller, prefix, owner, target, "balance");
	}
	
	public static void getRecent(Player caller, String prefix, String owner, String target) {
		// Monitor the calling players recent transactions
		economy.monitor(caller, prefix, owner, target, "balance");
	}

	public static void getTop(Player caller, String prefix, String owner, String target) {
		// Monitor the top account balances
		economy.monitor(caller, prefix, owner, target, "balance");
	}

	public static void getSpecific(Player caller, String prefix, String owner, String target) {
		// Monitor a set of transactions involving the given owner and target
		economy.monitor(caller, prefix, owner, target, "balance");
	}
	
	public static void create(String prefix, String owner, double amount) {
		// Attempt to create an account for the given owner with the given starting amount
		economy.create(prefix, owner, amount);
	}
}
