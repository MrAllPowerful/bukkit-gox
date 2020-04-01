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
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXSit implements Listener {
	private GoX plugin;
	private Logger logger;
	
	public GoXSit(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
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
		
		cart.setMaxSpeed(0.4*plugin.getCartMaxSpeed());
		
		GoXPlayer gp = new GoXPlayer(player, plugin);
		String destination = gp.getDestination();
		
		if (destination == null) {
			return;
		}
		
		Block block = cart.getLocation().getBlock();
		if (!GoXUtils.isRails(block)) {
			return;
		}
		if (!GoXUtils.isCartOverBlock(cart, plugin.getStationBlock()) &&
				!GoXUtils.isCartOverBlock(cart, plugin.getNodeBlock())) {
			return;
		}
		
		GoXNode node = GoXMap.GetNode(cart.getLocation());
		if (node == null) {
			return;
		}
		
		String startDirection = GoXUtils.repathRoutine(gp, node);
		
		if (startDirection != null) {
			GoXUtils.turnRail(cart, startDirection);
			GoXUtils.pushCart(cart, startDirection);
		}
		
	}
}
