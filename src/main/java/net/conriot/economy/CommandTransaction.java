package net.conriot.economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandTransaction implements TransactionCallback {
	private Player caller;
	private String command;
	private String[] args;
	
	public CommandTransaction(Player caller, String command, String[] args) {
		this.caller = caller;
		this.command = command;
	}
	
	@Override
	public void complete(Status result, Transaction transaction) {
		switch(command.toLowerCase()) {
		case "eco pay":
			switch(result) {
			case SUCCESS:
				caller.sendMessage(ChatColor.GREEN + "You have paid " + ChatColor.DARK_GREEN + args[])
				break;
			}
			break;
		case "eco give":
			break;
		case "eco take":
			break;
		}
	}

}
