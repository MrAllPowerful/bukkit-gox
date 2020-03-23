package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXSit implements Listener {
	private GoX plugin;
	private Logger logger;
	private ConfigurationSection config;
	
	public GoXSit(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
		this.config = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
	}
	
	@EventHandler
	public void onCartSit(VehicleEnterEvent e){
		if (!(e.getVehicle() instanceof Minecart)) {
			return;
		}
		if (!(e.getEntered() instanceof Player)) {
			return;
		}
		
		Minecart cart = (Minecart) e.getVehicle();
		Player player = (Player) e.getEntered();
		
		Block block = cart.getLocation().getBlock();
		if (!GoXUtils.isRails(block)) {
			return;
		}
		if (!GoXUtils.isCartOverBlock(cart, Material.NETHERRACK) &&
				!GoXUtils.isCartOverBlock(cart, Material.NETHERRACK)) {
			return;
		}
		GoXNode node = GoXMap.GetNode((int) block.getX(), (int) block.getZ());
		if (node == null) {
			return;
		}
		
		GoXPlayer p = new GoXPlayer(player, plugin);
		
		String destination = p.getDestination();
		
		if (destination != null) {
			player.sendMessage(ChatColor.YELLOW+config.getString("searching path"));
			GoXPath path = GoXMap.FindPath(node.getId(), destination);
			p.setPath(path);
			if (path == null || path.IsEmpty()) {
				p.resetPath();
				player.sendMessage(ChatColor.RED+config.getString("path not found"));
				return;
			}
			String startDirection = p.popPath();
			p.setNext(startDirection);
			p.setPath(path);
			player.sendMessage(ChatColor.GREEN+config.getString("path found"));
			Vector v = GoXUtils.getVector(startDirection).multiply(cart.getMaxSpeed()*0.7);
			cart.setVelocity(v);
		}
		else {
			p.resetPath();
			player.sendMessage(ChatColor.RED+config.getString("no destination"));
		}
	}
}
