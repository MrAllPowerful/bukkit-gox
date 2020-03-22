package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
import com.radiantai.gox.pathfinding.GoPath;
import com.radiantai.gox.pathfinding.Utils;
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
		
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		
		if (!Utils.isOnRails(cart) || !Utils.hasPlayer(cart)) {
			return;
		}
		
		Player player = (Player) cart.getPassenger();
		
		Material blockUnder = cart.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		switch(blockUnder){
		case BRICK:
		case NETHERRACK:
			String dirBlock = getPlayerNextDirection(player);
			
			if (dirBlock == null || dirBlock.isEmpty()) {
				return;
			}
			
			//player.sendMessage(ChatColor.GREEN + "Going "+dirBlock+"!");
			
			turnRail(cart, dirBlock);
			
			setMinecartDirection(cart, dirBlock);
			
			removeNextDirection(player);
			
			break;
		default:
			String dirOther = getPlayerNextDirection(player);
			if (dirOther == null || dirOther.isEmpty()) {
				setPlayerNextDirection(player, getFromPath(player));
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
			railsState.setDirection(Utils.getBlockFace(dir), false);
			state.setData(railsState);
			state.update();
		}
	}
	
	private boolean isAngle(Block rail, Rails state) {
		if (state.isCurve()) {
			boolean north, east, south, west;
			north = Utils.isRails(rail.getRelative(BlockFace.NORTH));
			east = Utils.isRails(rail.getRelative(BlockFace.EAST));
			south = Utils.isRails(rail.getRelative(BlockFace.SOUTH));
			west = Utils.isRails(rail.getRelative(BlockFace.WEST));
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
		Vector newDirection = Utils.getVector(dir).multiply(speed);
		cart.setVelocity(newDirection);
	}
	
	private String getPlayerNextDirection(Player player) {
		if (player.hasMetadata("go_next")) {
			String direction = player.getMetadata("go_next").get(0).asString();
			return direction;
		}
		return null;
	}
	
	private void setPlayerNextDirection(Player player, String dir) {
		if (player.hasMetadata("go_next")) {
			player.removeMetadata("go_next", plugin);
			player.setMetadata("go_next", new FixedMetadataValue(plugin, dir));
		}
		else {
			player.setMetadata("go_next", new FixedMetadataValue(plugin, dir));
		}
	}
	
	private String getFromPath(Player player) {
		if (player.hasMetadata("go_path")) {
			GoPath path = (GoPath) player.getMetadata("go_path").get(0).value();
			if (path.IsEmpty()) {
				Utils.resetPathMeta(player, plugin);
				if (player.hasMetadata("go_next")) {
					player.removeMetadata("go_next", plugin);
				}
				return null;
			}
			String direction = path.Pop();
			player.removeMetadata("go_path", plugin);
			player.setMetadata("go_path", new FixedMetadataValue(plugin, path));
			return direction;
		}
		return null;
	}
	
	private void removeNextDirection(Player player) {
		if (player.hasMetadata("go_next")) {
			player.removeMetadata("go_next", plugin);
		}
	}
}
