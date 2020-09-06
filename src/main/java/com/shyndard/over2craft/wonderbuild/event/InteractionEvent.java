package com.shyndard.over2craft.wonderbuild.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.shyndard.over2craft.wonderbuild.service.ArenaService;
import com.shyndard.over2craft.wonderbuild.service.GameService;

public class InteractionEvent implements Listener {

	@EventHandler
	public void breakBlock(BlockBreakEvent event) {
		if(GameService.getInstance().getCurrentBuilder() == event.getPlayer() && ArenaService.getInstance().isInside(event.getBlock().getLocation())) return;
		System.out.println("BlockBreakEvent > cancel");
		event.setCancelled(true);
	}

	@EventHandler
	public void placeBlock(BlockPlaceEvent event) {
		if(GameService.getInstance().getCurrentBuilder() == event.getPlayer() && ArenaService.getInstance().isInside(event.getBlock().getLocation())) return;
		System.out.println("BlockPlaceEvent > cancel");
		event.setCancelled(true);
	}

	@EventHandler
	public void looseFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(EntityInteractEvent event) {
		if(GameService.getInstance().getCurrentBuilder() == event.getEntity() && ArenaService.getInstance().isInside(event.getBlock().getLocation())) return;
		System.out.println("EntityInteractEvent > cancel");
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK && event.getPlayer().isOp()) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				ArenaService.getInstance().setSelector(event.getPlayer(), event.getClickedBlock().getLocation());
				event.setCancelled(true);
			}
		}
	}
}
