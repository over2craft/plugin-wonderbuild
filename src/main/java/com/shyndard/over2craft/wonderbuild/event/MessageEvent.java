package com.shyndard.over2craft.wonderbuild.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.shyndard.over2craft.wonderbuild.service.GameService;
import com.shyndard.over2craft.wonderbuild.service.ScoreService;

import net.md_5.bungee.api.ChatColor;

public class MessageEvent implements Listener {

	@EventHandler
	public void onPlayerJoin(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (player == GameService.getInstance().getCurrentBuilder()) {
			player.sendMessage(ChatColor.RED + "Tu ne peux pas parler pendant que tu es builder.");
			event.setCancelled(true);
		} else {
			if (event.getMessage().equalsIgnoreCase(GameService.getInstance().getCurrentWord())) {
				ScoreService.getInstance().findWord(player);
				event.setCancelled(true);
			} else {
				event.setFormat(String.format(ChatColor.YELLOW + "%s" + ChatColor.GRAY + ": %s", player.getDisplayName(), event.getMessage()));
			}
		}
	}

}
