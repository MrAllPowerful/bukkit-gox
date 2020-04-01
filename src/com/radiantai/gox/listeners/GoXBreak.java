package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXUtils;

public class GoXBreak implements Listener {
	private GoX plugin;
	private Logger logger;
	
	public GoXBreak(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Block block = e.getBlock();
		if (block.getType() != plugin.getNodeBlock() && block.getType() != plugin.getStationBlock()) {
			return;
		}
		Location location = block.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		if (node != null && node.getY() == (int) location.getY()) {
			e.getPlayer().sendMessage(ChatColor.RED+GoXChat.chat("cannot break"));
			e.setCancelled(true);
		}
	}
}