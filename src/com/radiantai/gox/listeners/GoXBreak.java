package com.radiantai.gox.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;

public class GoXBreak implements Listener {
	private GoX plugin;
	
	public GoXBreak(GoX plugin) {
		this.plugin = plugin;
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