package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXAddNode implements Listener {
	private GoX plugin;
	private Logger logger;
	
	public GoXAddNode(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler
	public void onRailPlaced(BlockPlaceEvent e){
		Block block = e.getBlock();
		if (block.getType() != Material.RAILS) {
			return;
		}
		Block under = block.getRelative(BlockFace.DOWN);
		Player player = e.getPlayer();
		if (under.getType() == Material.BRICK) {
			new GoXPlayer(player, plugin).setAddNode(under.getLocation());
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("enter add node") + ChatColor.WHITE + "/gom add");
		}
	}
}
