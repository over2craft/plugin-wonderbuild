package com.shyndard.over2craft.wonderbuild.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class ScoreService {

	private static ScoreService instance;
	private Map<Player, Integer> playerScore = new HashMap<>();

	private List<Player> currentWordPlayerFound = new ArrayList<>();

	public static ScoreService getInstance() {
		if (instance == null) {
			instance = new ScoreService();
		}
		return instance;
	}

	public void nextWord() {
		currentWordPlayerFound.clear();
	}

	public void findWord(Player player) {
		if (currentWordPlayerFound.contains(player)) {
			TitleService.getInstance().send(player, "", ChatColor.RED + "Tu as déjà trouvé ce mot", 0, 40, 10);
			return;
		}
		TitleService.getInstance().send(player, "",
				ChatColor.GREEN + "Tu as trouvé le mot " + GameService.getInstance().getCurrentWord(), 0, 60, 10);
		Bukkit.broadcastMessage(ChatColor.GRAY + player.getName() + " a trouvé le mot secret.");
		Integer value = playerScore.get(player);
		if (value == null) {
			playerScore.put(player, 10 - currentWordPlayerFound.size() > 0 ? 10 - currentWordPlayerFound.size() : 0);
		} else {
			playerScore.put(player,
					value + (10 - currentWordPlayerFound.size() > 0 ? 10 - currentWordPlayerFound.size() : 0));
		}
		currentWordPlayerFound.add(player);
		if (currentWordPlayerFound.size() == GameService.getInstance().getPlayerList().size() - 1) {
			GameService.getInstance().setTimer(0);
		}
	}

	public void announceResults() {
		Player winner = getWinner();
		Bukkit.broadcastMessage(ChatColor.GREEN + winner.getName() + ChatColor.GOLD + " gagne avec " + ChatColor.AQUA
				+ playerScore.get(winner) + ChatColor.GOLD + " pts.");
		playerScore.forEach((player, score) -> {
			player.sendMessage(ChatColor.GRAY + "Tu as " + ChatColor.GREEN + (score == null ? 0 : score)
					+ ChatColor.GRAY + " points");
		});
		Bukkit.broadcastMessage("");
	}

	private Player getWinner() {
		Player winner = null;
		int bestScore = 0;
		for (Entry<Player, Integer> keySet : playerScore.entrySet()) {
			if (winner == null || keySet.getValue() > bestScore) {
				winner = keySet.getKey();
			}
		}
		return winner;
	}

	public List<Player> getCurrentWordPlayerFound() {
		return currentWordPlayerFound;
	}

}
