package com.shyndard.over2craft.wonderbuild.service;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.shyndard.over2craft.wonderbuild.MainPlugin;
import com.shyndard.over2craft.wonderbuild.entity.GameState;

public class AutoExecuteService {

	private static AutoExecuteService instance;

	public static AutoExecuteService getInstance() {
		if (instance == null) {
			instance = new AutoExecuteService();
		}
		return instance;
	}

	public AutoExecuteService() {
		run();
	}

	public static void run() {
		new BukkitRunnable() {
			@Override
			public void run() {
				GameService gameService = GameService.getInstance();
				if(gameService.getState() == GameState.WAITING_TO_START && Bukkit.getOnlinePlayers().size() >= gameService.getMinPlayer()) {
					gameService.launchStartCountdown();
				} if(gameService.getState() == GameState.STOPING) {
					if(Bukkit.getOnlinePlayers().isEmpty()) {
						Bukkit.shutdown();
					}
				}
				else {
					gameService.removeOneSecond();
				}
			}
		}.runTaskTimer(MainPlugin.getInstance(), 0, 20L);
	}

}