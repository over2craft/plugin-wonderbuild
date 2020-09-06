package com.shyndard.over2craft.wonderbuild.service;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.shyndard.over2craft.wonderbuild.MainPlugin;

public class ArenaService {

	private static ArenaService instance;

	private Location firstArenaCorner;
	private Location secondArenaCorner;

	private Map<Player, Location> selector = new HashMap<>();

	public static ArenaService getInstance() {
		if (instance == null) {
			instance = new ArenaService();
		}
		return instance;
	}

	public void load() {
		firstArenaCorner = loadLocation("party.arena.first");
		secondArenaCorner = loadLocation("party.arena.second");
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
		return new Location(world, x, y, z);
	}

	public void reset() {
		if (firstArenaCorner == null || secondArenaCorner == null) {
			Bukkit.broadcastMessage("No arena defined");
			return;
		}
		int x1 = firstArenaCorner.getBlockX();
		int y1 = firstArenaCorner.getBlockY();
		int z1 = firstArenaCorner.getBlockZ();

		int x2 = secondArenaCorner.getBlockX();
		int y2 = secondArenaCorner.getBlockY();
		int z2 = secondArenaCorner.getBlockZ();

		int xMin = x1 < x2 ? x1 : x2;
		int xMax = x1 > x2 ? x1 : x2;
		int yMin = y1 < y2 ? y1 : y2;
		int yMax = y1 > y2 ? y1 : y2;
		int zMin = z1 < z2 ? z1 : z2;
		int zMax = z1 > z2 ? z1 : z2;

		secondArenaCorner.getWorld().getEntities().forEach(entity -> {
			if (entity.getType() != EntityType.PLAYER && ArenaService.getInstance().isInside(entity.getLocation())) {
				entity.remove();
			}
		});

		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					firstArenaCorner.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}

	public void setSelector(Player player, Location location) {
		selector.put(player, location);
		player.sendMessage("Selector set. Save it with /wonderbuild setarena <first|second>");
	}

	public Location getSelector(Player player) {
		return selector.get(player);
	}

	public boolean isInside(Location target) {
		return (target.getBlockX() >= firstArenaCorner.getBlockX()
				&& target.getBlockX() <= secondArenaCorner.getBlockX()
				|| target.getBlockX() >= secondArenaCorner.getBlockX()
						&& target.getBlockX() <= firstArenaCorner.getBlockX())
				&& (target.getBlockY() >= firstArenaCorner.getBlockY()
						&& target.getBlockY() <= secondArenaCorner.getBlockY()
						|| target.getBlockY() >= secondArenaCorner.getBlockY()
								&& target.getBlockY() <= firstArenaCorner.getBlockY())
				&& (target.getBlockZ() >= firstArenaCorner.getBlockZ()
						&& target.getBlockZ() <= secondArenaCorner.getBlockZ()
						|| target.getBlockZ() >= secondArenaCorner.getBlockZ()
								&& target.getBlockZ() <= firstArenaCorner.getBlockZ());
	}
}
