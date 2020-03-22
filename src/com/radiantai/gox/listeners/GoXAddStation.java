package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.radiantai.gox.GoX;

public class GoXAddStation implements Listener {
	private GoX plugin;
	private Logger logger;
	
	public GoXAddStation(GoX plugin) {
		this.plugin = plugin;
		logger = Logger.getLogger("Minecraft");
	}
	
	@EventHandler
	public void onRailPlaced(BlockPlaceEvent e){
		Block block = e.getBlock();
		if (block.getType() != Material.RAILS) {
			return;
		}
		Block under = block.getRelative(BlockFace.DOWN);
		Player player = e.getPlayer();
		if (under.getType() == Material.NETHERRACK) {
			player.setMetadata("go_add_station", new FixedMetadataValue(plugin, under.getLocation()));
			player.sendMessage(
					ChatColor.GREEN + "Enter " + ChatColor.WHITE + "/gom addstation <station name>"
			+ ChatColor.GREEN + " to add this "+ ChatColor.WHITE +" station "+ ChatColor.GREEN +" to the map.");
		}
	}
}
