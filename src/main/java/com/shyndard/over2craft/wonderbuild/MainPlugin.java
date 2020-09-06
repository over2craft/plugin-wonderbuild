package com.shyndard.over2craft.wonderbuild;

import org.bukkit.plugin.java.JavaPlugin;

import com.shyndard.over2craft.wonderbuild.command.WonderbuildCmd;
import com.shyndard.over2craft.wonderbuild.event.ConnectionEvent;
import com.shyndard.over2craft.wonderbuild.event.InteractionEvent;
import com.shyndard.over2craft.wonderbuild.event.MessageEvent;
import com.shyndard.over2craft.wonderbuild.event.WorldEvent;
import com.shyndard.over2craft.wonderbuild.service.ArenaService;
import com.shyndard.over2craft.wonderbuild.service.AutoExecuteService;
import com.shyndard.over2craft.wonderbuild.service.GameService;

public class MainPlugin extends JavaPlugin {

	private static MainPlugin instance;

	public static MainPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		// Init config file
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		// Init services
		GameService.getInstance().load();
		ArenaService.getInstance().load();
		AutoExecuteService.getInstance();

		// Init event
		getServer().getPluginManager().registerEvents(new ConnectionEvent(), this);
		getServer().getPluginManager().registerEvents(new InteractionEvent(), this);
		getServer().getPluginManager().registerEvents(new MessageEvent(), this);
		getServer().getPluginManager().registerEvents(new WorldEvent(), this);

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord"); 
		
		// Register command
		this.getCommand("wonderbuild").setExecutor(new WonderbuildCmd());
	}

	@Override
	public void onDisable() {

	}
}