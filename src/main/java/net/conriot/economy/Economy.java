package net.conriot.economy;

import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends JavaPlugin {
	private EconomyManager economy;
	
	@Override
	public void onEnable() {
		this.economy = new EconomyManager();
	}
	
	@Override
	public void onDisable() {
		// Nothing to do here
	}
}
