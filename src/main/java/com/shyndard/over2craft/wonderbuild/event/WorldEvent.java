package com.shyndard.over2craft.wonderbuild.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvent implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState())
			event.setCancelled(true);
	}

	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {
		if (event.toThunderState())
			event.setCancelled(true);
	}
}
