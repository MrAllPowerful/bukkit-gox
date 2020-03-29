package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Rails;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXStation;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXPlayer;
import com.radiantai.gox.structures.RollingQueue;

public class GoXMovement implements Listener {
	
	private GoX plugin;
	private Logger logger;
	
	public GoXMovement(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler
	public void onMinecartMove(VehicleMoveEvent e){
		
		if (!shouldTrack(e)) {
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		Player player = (Player) cart.getPassenger();
		GoXPlayer gp = new GoXPlayer(player, plugin);
		
		if (gp.getDestination() == null) {
			return;
		}
		
		Location location = cart.getLocation();
		Material blockUnderType = location.getBlock().getRelative(BlockFace.DOWN).getType();
		GoXNode node = GoXMap.GetNode(location);
		
		if (hasArrived(gp)) {
			gp.reset();
			player.sendMessage(ChatColor.YELLOW+GoXChat.chat("arrived"));
			GoXUtils.stopCart(cart);
			if (node instanceof GoXStation) {
				Location drop = ((GoXStation) node).getDropPoint();
				if (drop != null) {
					player.leaveVehicle();
					player.teleport(drop);
					cart.remove();
					player.getInventory().addItem(new ItemStack(Material.MINECART));
				}
			}
			GoXChat.fancyStationCompact(player, node);
			return;
		}
		
		switch(blockUnderType){
		case BRICK:
		case NETHERRACK:
			
			if (node == null) {
				return;
			}
			
			String nextDir = gp.getNext();
			
			if(needRepath(gp, node)) {
				GoXUtils.stopCart(cart);
				gp.resetPath();
				
				String startDirection = GoXUtils.repathRoutine(gp, node);
				
				GoXUtils.turnRail(cart, startDirection);
				GoXUtils.pushCart(cart, startDirection);
				return;
			}
			
			if (nextDir == null) {
				return;
			}
			
			GoXUtils.turnRail(cart, nextDir);
			GoXUtils.setMinecartDirection(cart, nextDir);
			
			gp.setNext(null);
			gp.setExpected(node.getLink(nextDir) != null ? node.getLink(nextDir).getId() : null);
			
			break;
		default:
			String dirOther = gp.getNext();
			if (dirOther == null) {
				gp.setNext(gp.popPath());
				return;
			}
			return;
		}
	}
	
	private boolean shouldTrack(VehicleMoveEvent e) {
		if(!(e.getVehicle() instanceof Minecart)){
			return false;
		}
		Minecart cart = (Minecart)e.getVehicle();
		if (!GoXUtils.isOnRails(cart) || !GoXUtils.hasPlayer(cart)) {
			return false;
		}
		return true;
	}
	
	private boolean hasArrived(GoXPlayer gp) {
		
		Block block = gp.getPlayer().getVehicle().getLocation().getBlock().getRelative(BlockFace.DOWN);
		Location nodeLocation = block.getLocation();
		
		GoXNode node = GoXMap.GetNode(nodeLocation);
		String destination = gp.getDestination();
		
		if (destination != null) {
			if (node != null && node.getId().equals(destination)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean needRepath(GoXPlayer gp, GoXNode currNode) {
		String expected = gp.getExpected();
		
		if (gp.getPath() == null) {
			return true;
		}
		
		if (!currNode.getId().equals(expected) && gp.getNext() != null) {
			gp.getPlayer().sendMessage(ChatColor.GRAY+GoXChat.chat("repath"));
			return true;
		}
		
		return false;
	}
}
