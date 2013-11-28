package net.conriot.economy;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

class Commands implements CommandExecutor {
	private EconomyManager economy;
	
	public Commands(EconomyManager economy) {
		this.economy = economy;
	}
	
	private void bal(Player player) {
		this.economy.monitor(player, "", player.getName(), "", "balance");
	}
	
	private void baltop(Player player) {
		this.economy.monitor(player, "", player.getName(), "", "top");
	}
	
	private void pay(Player player, String[] args) {
		if(args.length >= 3) {
			try {
				this.economy.pay("", player.getName(), "", args[1], Double.parseDouble(args[2]), callback);
			} catch(NumberFormatException e) {
				return;
			}
		}
	}
	
	private void eco(CommandSender sender, Command command, String label, String[] args) {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			switch(label.toLowerCase()) {
			case "bal":
				bal(sender, command, label, args);
				break;
			case "baltop":
				baltop(sender, command, label, args);
				break;
			case "pay":
				pay(sender, command, label, args);
				break;
			case "eco":
				eco(sender, command, label, args);
				break;
			}
		// Always return true to prevent default Bukkit messages
		return true;
	}
}
