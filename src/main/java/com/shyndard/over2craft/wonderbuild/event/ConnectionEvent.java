package com.shyndard.over2craft.wonderbuild.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.shyndard.over2craft.wonderbuild.entity.GameState;
import com.shyndard.over2craft.wonderbuild.service.GameService;

public class ConnectionEvent implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		if (GameService.getInstance().getState() == GameState.IN_PROGRESS
				|| GameService.getInstance().getState() == GameState.ENDING
				|| GameService.getInstance().getMaxPlayer() < Bukkit.getOnlinePlayers().size()) {
			player.setGameMode(GameMode.SPECTATOR);
		} else {
			player.setGameMode(GameMode.ADVENTURE);
			Bukkit.broadcastMessage(ChatColor.GRAY + player.getName() + " a " + ChatColor.GREEN + "rejoint"
					+ ChatColor.GRAY + " la partie.");
		}
		if (GameService.getInstance().getGuesserSpawn() == null) {
			player.sendMessage(ChatColor.RED + "Aucun spawn défini.");
		} else {
			if(GameService.getInstance().getState() == GameState.STARTING_COUNTDOWN) {
				player.sendActionBar(ChatColor.GREEN + "Démarrage en cours");
			} else {
				player.sendActionBar(ChatColor.RED + "En attente de joueurs");
			}
			player.teleport(GameService.getInstance().getGuesserSpawn());
		}
		player.sendMessage(ChatColor.GOLD + "Bienvenue sur le " + ChatColor.BOLD + "WonderBuild");
		player.sendMessage(ChatColor.YELLOW + "Ce jeu est en " + ChatColor.AQUA + "bêta" + ChatColor.YELLOW
				+ ". Si tu trouves un bug ou si tu as une idée d'amélioration, envoie nous un message sur le discord.");

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		GameService gameService = GameService.getInstance();
		if (gameService.getState() == GameState.STARTING_COUNTDOWN
				&& Bukkit.getOnlinePlayers().size() <= gameService.getMinPlayer()) {
			gameService.cancelCountdown();
		} else if (event.getPlayer() == gameService.getCurrentBuilder()) {
			gameService.next();
		} else if (gameService.getState() == GameState.IN_PROGRESS && gameService.getPlayerList().size() <= 1) {
			gameService.end();
		}
		Bukkit.broadcastMessage(ChatColor.GRAY + event.getPlayer().getName() + " a " + ChatColor.RED + "quitté"
				+ ChatColor.GRAY + " la partie.");
	}

	@EventHandler
	public void onKick(PlayerLoginEvent event) {
		if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
			event.setKickMessage(ChatColor.RED + "La partie redémarre. Essaye à nouveau dans quelques minutes.");
		}
	}
}
