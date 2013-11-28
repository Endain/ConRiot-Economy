package net.conriot.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

class EconomyManager implements Listener {
	public EconomyManager(Plugin plugin) {
		// Register all events
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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
		// Create a new Transaction an pass it to a Validator
		new Validator(new Transaction(fromPrefix, from, toPrefix, to, amount), callback);
	}
	
	public void monitor(Player caller, String prefix, String owner, String target, String type) {
		// Create a Monitor object and select an action to perform using it
		Monitor m = new Monitor(caller, prefix, owner, target);
		
		switch(type.toLowerCase()) {
		case "balance":
			m.listSelf();
			break;
		case "recent":
			m.listRecent();
			break;
		case "top":
			m.listTop();
			break;
		case "specific":
			m.listSpecific();
			break;
		}
	}
	
	public void create(String prefix, String owner, double amount) {
		// Attempt to create an account for the given owner with the given starting amount
		new Creator(prefix, owner, amount);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// Attempt to create an account for the player who has just joined
		create("", event.getPlayer().getName(), 2500);
	}
}
