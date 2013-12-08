package net.conriot.sona.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class CommandTransaction implements TransactionCallback {
	private CommandSender caller;
	private String command;
	private String[] args;
	
	public CommandTransaction(CommandSender caller, String command, String[] args) {
		this.caller = caller;
		this.command = command;
	}
	
	@Override
	public void complete(Status result, Transaction transaction) {
		switch(command.toLowerCase()) {
		case "eco pay":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have paid $" + ChatColor.DARK_GREEN + args[2] + ChatColor.GREEN + " to '" + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + "'!");
				break;
			case INSUFFICIENT_FUNDS:
				caller.sendMessage(ChatColor.RED + "You do not have enough funds to pay $" + ChatColor.DARK_RED + args[2] + ChatColor.RED + " to '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "'!");
				break;
			case UNKNOWN_TO:
				caller.sendMessage(ChatColor.RED + "No target by the name of '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not complete transaction!");
				break;
			}
			break;
		case "eco give":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have given $" + ChatColor.DARK_GREEN + args[2] + ChatColor.GREEN + " to '" + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not give funds to '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "'!");
				break;
			}
			break;
		case "eco take":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have taken $" + ChatColor.DARK_GREEN + args[2] + ChatColor.GREEN + " from '" + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + "'!");
				break;
			default:
				caller.sendMessage(ChatColor.RED + "Could not give funds to '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "'!");
				break;
			}
		}
	}

}
