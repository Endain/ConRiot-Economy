package net.conriot.sona.economy;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends JavaPlugin {
	private static EconomyManager economy;
	private static DecimalFormat df;
	
	@Override
	public void onEnable() {
		// Register the economy managed which will handle all transaction processing
		economy = new EconomyManager(this);
		
		// Create a DecimalFormat object to handle money values
		df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		df.setMinimumIntegerDigits(1);
		
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
	
	public static void checkBalance(Player caller, TransactionCallback callback) {
		// Check and return the current balance
		economy.monitor(caller, callback);
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
	
	public static String format(double value) {
		return df.format(value);
	}
}
