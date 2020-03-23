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
import org.bukkit.material.Rails;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXPlayer;
import com.radiantai.gox.structures.RollingQueue;

public class GoXMovement implements Listener {
	
	private GoX plugin;
	private Logger logger;
	private ConfigurationSection chatConfig;
	
	public GoXMovement(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
		this.chatConfig = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
	}
	
	@EventHandler
	public void onMinecartMove(VehicleMoveEvent e){
		
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		
		if (!GoXUtils.isOnRails(cart) || !GoXUtils.hasPlayer(cart)) {
			return;
		}
		
		Player player = (Player) cart.getPassenger();
		GoXPlayer gp = new GoXPlayer(player, plugin);
		
		Location location = cart.getLocation();
		Block block = location.getBlock().getRelative(BlockFace.DOWN);
		Location nodeLocation = block.getLocation();
		Material blockUnderType = block.getType();
		GoXNode node = GoXMap.GetNode((int) nodeLocation.getX(), (int) nodeLocation.getZ());
		String destination = gp.getDestination();
		
		if (destination != null) {
			if (node != null && node.getId().equals(destination)) {
				//logger.info("Arrived!");
				gp.reset();
				player.sendMessage(ChatColor.GREEN+chatConfig.getString("arrived")+node);
				GoXUtils.stopCart(cart);
				return;
			}
		}
		
		switch(blockUnderType){
		case BRICK:
		case NETHERRACK:
			//logger.info("Special!");
			String nextDir = gp.getNext();
			
			if (nextDir == null || nextDir.isEmpty()) {
				return;
			}
			
			turnRail(cart, nextDir);
			
			setMinecartDirection(cart, nextDir);
			
			gp.setNext(null);
			
			break;
		default:
			//logger.info("Other!");
			String dirOther = gp.getNext();
			if (dirOther == null) {
				//logger.info("Set next!");
				gp.setNext(gp.popPath());
				return;
			}
			return;
		}
	}
	
	private void turnRail(Minecart cart, String dir) {
		Block rail = cart.getLocation().getBlock();
		BlockState state = rail.getState();
		Rails railsState = (Rails) state.getData();
		if (!isAngle(rail, railsState)) {
			railsState.setDirection(GoXUtils.getBlockFace(dir), false);
			state.setData(railsState);
			state.update();
		}
	}
	
	private boolean isAngle(Block rail, Rails state) {
		if (state.isCurve()) {
			boolean north, east, south, west;
			north = GoXUtils.isRails(rail.getRelative(BlockFace.NORTH));
			east = GoXUtils.isRails(rail.getRelative(BlockFace.EAST));
			south = GoXUtils.isRails(rail.getRelative(BlockFace.SOUTH));
			west = GoXUtils.isRails(rail.getRelative(BlockFace.WEST));
			if (north && east && !west && !south) return true;
			if (north && west && !east && !south) return true;
			if (south && east && !west && !north) return true;
			if (south && west && !east && !north) return true;
		}
		return false;
	}
	
	private void setMinecartDirection(Minecart cart, String dir) {
		Vector velocity = cart.getVelocity();
		double speed = velocity.length();
		Vector newDirection = GoXUtils.getVector(dir).multiply(speed);
		cart.setVelocity(newDirection);
	}
}
