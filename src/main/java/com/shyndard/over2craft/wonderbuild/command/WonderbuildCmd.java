package com.shyndard.over2craft.wonderbuild.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.shyndard.over2craft.wonderbuild.MainPlugin;
import com.shyndard.over2craft.wonderbuild.service.ArenaService;
import com.shyndard.over2craft.wonderbuild.service.GameService;

public class WonderbuildCmd implements CommandExecutor {

	private static final String noPermission = "Tu n'as pas la permission.";
	private static final String playerOnly = "Player command only.";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) {
			if (!sender.hasPermission("wonderbuild.admin") && !sender.isOp()) {
				sender.sendMessage(noPermission);
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(playerOnly);
				return true;
			}
			if ("guesser".equalsIgnoreCase(args[1]) || "builder".equalsIgnoreCase(args[1])) {
				Player player = (Player) sender;
				String prefix = "party.spawn." + args[1].toLowerCase() + ".";
				MainPlugin instance = MainPlugin.getInstance();
				instance.getConfig().set(prefix + "world", player.getWorld().getName());
				instance.getConfig().set(prefix + "x", player.getLocation().getX());
				instance.getConfig().set(prefix + "y", player.getLocation().getY());
				instance.getConfig().set(prefix + "z", player.getLocation().getZ());
				instance.getConfig().set(prefix + "yaw", player.getLocation().getYaw());
				instance.getConfig().set(prefix + "pitch", player.getLocation().getPitch());
				instance.saveConfig();
				sender.sendMessage(ChatColor.GREEN + args[1] + " spawn set.");
				GameService.getInstance().load();
			} else {
				sender.sendMessage("Usage : /wonderbuild setspawn <builder|guesser>");
			}
			return true;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setarena")) {
			if (!sender.hasPermission("wonderbuild.admin") && !sender.isOp()) {
				sender.sendMessage(noPermission);
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(playerOnly);
				return true;
			}
			if ("first".equalsIgnoreCase(args[1]) || "second".equalsIgnoreCase(args[1])) {
				Player player = (Player) sender;
				String prefix = "party.arena." + args[1].toLowerCase() + ".";
				MainPlugin instance = MainPlugin.getInstance();
				Location selector = ArenaService.getInstance().getSelector(player);
				instance.getConfig().set(prefix + "world", selector.getWorld().getName());
				instance.getConfig().set(prefix + "x", selector.getBlockX());
				instance.getConfig().set(prefix + "y", selector.getBlockY());
				instance.getConfig().set(prefix + "z", selector.getBlockZ());
				instance.saveConfig();
				sender.sendMessage(ChatColor.GREEN + args[1] + " arena location set.");
				ArenaService.getInstance().load();
			} else {
				sender.sendMessage("Usage : /wonderbuild setarena <first|second>");
			}
			return true;
		}
		return false;
	}
}