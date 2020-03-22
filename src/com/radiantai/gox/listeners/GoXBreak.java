package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoMap;
import com.radiantai.gox.pathfinding.GoNode;

public class GoXBreak implements Listener {
	private GoX plugin;
	private Logger logger;
	
	public GoXBreak(GoX plugin) {
		this.plugin = plugin;
		logger = Logger.getLogger("Minecraft");
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Block block = e.getBlock();
		if (block.getType() != Material.BRICK && block.getType() != Material.NETHERRACK) {
			return;
		}
		Location location = block.getLocation();
		GoNode node = GoMap.GetNode((int) location.getX(), (int) location.getZ());
		if (node != null && node.getY() == (int) location.getY()) {
			e.getPlayer().sendMessage(ChatColor.RED+"You cannot break a block registered as a node!");
			e.setCancelled(true);
		}
	}
}