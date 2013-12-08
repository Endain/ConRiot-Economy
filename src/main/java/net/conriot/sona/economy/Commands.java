package net.conriot.sona.economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				// Create a callback object so we can notify of success or failer
				CommandTransaction callback = new CommandTransaction(player, "eco pay", args);
				// Execute the payment
				this.economy.pay("", player.getName(), "", args[1], Double.parseDouble(args[2]), callback);
			} catch(NumberFormatException e) {
				return;
			}
		}
	}
	
	private void eco(CommandSender sender, Command command, String label, String[] args) {
		if(args.length >= 3) {
			try {
				// Determine which subcommand is being issued in order to act accordingly
				String subcommand = (label + " " + args[0]).toLowerCase();
				// Create a callback object so we can notify of success or failer
				CommandTransaction callback = new CommandTransaction(sender, subcommand, args);
				// Execute the given subcommand
				if(subcommand.equals("eco give") && sender.isOp())
					this.economy.givePlayer(args[1], Double.parseDouble(args[2]), callback);
				else if(subcommand.equals("eco take") && sender.isOp())
					this.economy.takePlayer(args[1], Double.parseDouble(args[2]), callback);
				else if(subcommand.equals("eco pay") && sender instanceof Player)
					this.economy.pay("", ((Player)sender).getName(), "", args[1], Double.parseDouble(args[2]), callback);
			} catch(NumberFormatException e) {
				return;
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			switch(label.toLowerCase()) {
			case "bal":
				bal((Player)sender);
				break;
			case "baltop":
				baltop((Player)sender);
				break;
			case "pay":
				pay((Player)sender, args);
				break;
			case "eco":
				eco(sender, command, label, args);
				break;
			}
		} else {
			if(label.toLowerCase().equals("eco"))
				eco(sender, command, label, args);
		}
		// Always return true to prevent default Bukkit messages
		return true;
	}
}
