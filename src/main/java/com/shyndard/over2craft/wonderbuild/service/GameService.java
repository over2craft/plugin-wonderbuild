package com.shyndard.over2craft.wonderbuild.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.shyndard.over2craft.wonderbuild.MainPlugin;
import com.shyndard.over2craft.wonderbuild.entity.GameState;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameService {

	private static GameService instance;
	private int maxPlayer;
	private int minPlayer;
	private int timePerBuilder;
	private int roundCount;
	private List<String> wordList;

	private Location guesserSpawn;
	private Location builderSPawn;

	private int currentRound = 0;
	private int currentTimer = 0;
	private Player currentBuilder;
	private String currentWord;

	private List<Player> roundPlayerList = new ArrayList<>();
	private List<String> roundWordList = new ArrayList<>();

	private GameState gameStatus;

	public static GameService getInstance() {
		if (instance == null) {
			instance = new GameService();
		}
		return instance;
	}

	public void load() {
		gameStatus = GameState.WAITING_TO_START;
		guesserSpawn = loadLocation("party.spawn.guesser");
		builderSPawn = loadLocation("party.spawn.builder");
		FileConfiguration config = MainPlugin.getInstance().getConfig();
		maxPlayer = config.getInt("party.max-player");
		minPlayer = config.getInt("party.min-player");
		timePerBuilder = config.getInt("party.time-per-builder");
		roundCount = config.getInt("party.round");
		wordList = config.getStringList("words");
		Bukkit.setWhitelist(false);
	}

	private Location loadLocation(String path) {
		FileConfiguration config = MainPlugin.getInstance().getConfig();
		String worldName = config.getString(path + ".world");
		if (worldName == null) {
			return null;
		}
		World world = Bukkit.getWorld(worldName);
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		int yaw = config.getInt(path + ".yaw");
		int pitch = config.getInt(path + ".pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	public void start() {
		gameStatus = GameState.IN_PROGRESS;
		next();
	}

	public void next() {
		if (currentWord != null) {
			Bukkit.broadcastMessage("Le mot secret était " + ChatColor.AQUA + ChatColor.BOLD + currentWord);
		}
		ArenaService.getInstance().reset();
		if (currentBuilder != null) {
			currentBuilder.getInventory().clear();
			currentBuilder.getActivePotionEffects().clear();
			currentBuilder.teleport(guesserSpawn);
			currentBuilder.setGameMode(GameMode.ADVENTURE);
		}
		if (roundWordList.isEmpty()) {
			roundWordList.addAll(wordList);
		}
		if (roundPlayerList.isEmpty()) {
			if (currentRound++ >= roundCount) {
				end();
				return;
			} else {
				roundPlayerList.addAll(Bukkit.getOnlinePlayers());
				Bukkit.broadcastMessage("Round " + currentRound);
			}
		}
		currentBuilder = roundPlayerList.remove(0);
		if (currentBuilder == null || !currentBuilder.isOnline()) {
			next();
			return;
		}
		ScoreService.getInstance().nextWord();
		currentTimer = timePerBuilder;
		chooseNextWord();
		currentBuilder.setGameMode(GameMode.CREATIVE);
		currentBuilder.teleport(builderSPawn);
		currentBuilder.sendMessage("Mot à faire deviner : " + ChatColor.AQUA + ChatColor.BOLD + currentWord);
		TitleService.getInstance().send(currentBuilder, ChatColor.AQUA + currentWord, "mot à faire deviner", 10, 60,
				10);
		getPlayerList().forEach(player -> {
			if (player != currentBuilder) {
				TitleService.getInstance().send(player, "", "devine le prochain mot", 10, 60, 10);
			}
		});
	}

	private void chooseNextWord() {
		currentWord = roundWordList.remove(new Random().nextInt(roundWordList.size() - 1));
	}

	public void end() {
		currentBuilder = null;
		gameStatus = GameState.ENDING;
		currentTimer = 10;
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Fin de la partie");
		Bukkit.broadcastMessage("");
		ScoreService.getInstance().announceResults();
		TitleService.getInstance().sendToAll(ChatColor.GOLD + "Fin de la partie", "", 10, 60, 10);
	}

	public void kickAllPlayers() {
		gameStatus = GameState.STOPING;
		Bukkit.setWhitelist(true);
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("Connect");
			out.writeUTF(MainPlugin.getInstance().getConfig().getString("kick-server"));
			out.close();
			b.close();
			Bukkit.getOnlinePlayers().forEach(player -> {
				player.sendPluginMessage(MainPlugin.getInstance(), "BungeeCord", b.toByteArray());
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Player getCurrentBuilder() {
		return currentBuilder;
	}

	public GameState getState() {
		return gameStatus;
	}

	public Location getGuesserSpawn() {
		return guesserSpawn;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public void removeOneSecond() {
		if (--currentTimer <= 0) {
			if (gameStatus == GameState.STARTING_COUNTDOWN) {
				start();
			} else if (gameStatus == GameState.IN_PROGRESS) {
				next();
			} else if (gameStatus == GameState.ENDING) {
				kickAllPlayers();
			}
		} else {
			if (gameStatus == GameState.STARTING_COUNTDOWN) {
				if (currentTimer <= 5) {
					TitleService.getInstance().sendToAll("", "début dans " + currentTimer + "s", 10, 60, 10);
				}
			} else if (gameStatus == GameState.IN_PROGRESS) {
				String messageToSent = ChatColor.RED + "Temps restant: " + ChatColor.GOLD + ChatColor.BOLD + currentTimer + "s" + ChatColor.GRAY + " - " + ChatColor.BLUE + ScoreService.getInstance().getCurrentWordPlayerFound().size() + "/" + (getPlayerList().size()-1) + " trouvé";
				Bukkit.getOnlinePlayers().forEach(player -> {
					player.sendActionBar(messageToSent);
				});
			}
		}
	}

	public void launchStartCountdown() {
		gameStatus = GameState.STARTING_COUNTDOWN;
		currentTimer = 20;
		Bukkit.broadcastMessage("Début de la partie dans " + currentTimer + "s.");
	}

	public void cancelCountdown() {
		gameStatus = GameState.WAITING_TO_START;
		Bukkit.broadcastMessage(ChatColor.RED + "Démarrage annulé. Pas assez de joueurs (min "
				+ GameService.getInstance().getMinPlayer() + ")");
	}

	public String getCurrentWord() {
		return currentWord;
	}

	public List<Player> getPlayerList() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> player.getGameMode() != GameMode.SPECTATOR)
				.collect(Collectors.toList());
	}

	public void setTimer(int timer) {
		currentTimer = timer;
	}
}
