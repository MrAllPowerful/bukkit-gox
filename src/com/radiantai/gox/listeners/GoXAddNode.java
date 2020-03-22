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

public class GoXAddNode implements Listener {
	private GoX plugin;
	private Logger logger;
	private ConfigurationSection config;
	
	public GoXAddNode(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
		this.config = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
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
			player.setMetadata("go_add", new FixedMetadataValue(plugin, under.getLocation()));
			player.sendMessage(ChatColor.GREEN + config.getString("enter add node") + ChatColor.WHITE + "/gom add");
		}
	}
}
