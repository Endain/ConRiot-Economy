package net.conriot.sona.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class CommandTransaction implements TransactionCallback {
	private CommandSender caller;
	private String command;
	private String target;
	private String amount;
	
	public CommandTransaction(CommandSender caller, String command, String target, String amount) {
		this.caller = caller;
		this.command = command;
		this.target = target;
		this.amount = amount;
	}
	
	@Override
	public void complete(Status result, Transaction transaction) {
		switch(command.toLowerCase()) {
		case "eco pay":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have paid $" + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " to '" + ChatColor.DARK_GREEN + target + ChatColor.GREEN + "'!");
				break;
			case INSUFFICIENT_FUNDS:
				caller.sendMessage(ChatColor.RED + "You do not have enough funds to pay $" + ChatColor.DARK_RED + amount + ChatColor.RED + " to '" + ChatColor.DARK_RED + target + ChatColor.RED + "'!");
				break;
			case UNKNOWN_TO:
				caller.sendMessage(ChatColor.RED + "No target by the name of '" + ChatColor.DARK_RED + target + ChatColor.RED + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not complete transaction!");
				break;
			}
			break;
		case "eco give":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have given $" + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " to '" + ChatColor.DARK_GREEN + target + ChatColor.GREEN + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not give funds to '" + ChatColor.DARK_RED + target + ChatColor.RED + "'!");
				break;
			}
			break;
		case "eco take":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have taken $" + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " from '" + ChatColor.DARK_GREEN + target + ChatColor.GREEN + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not give funds to '" + ChatColor.DARK_RED + target + ChatColor.RED + "'!");
				break;
			}
		}
	}

}
