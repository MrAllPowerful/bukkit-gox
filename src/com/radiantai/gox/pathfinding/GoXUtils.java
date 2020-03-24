package com.radiantai.gox.pathfinding;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXUtils {
	
	public static String GenerateId(int length) {
		String uuid = UUID.randomUUID().toString().substring(0, length-1);
		return uuid;
	}
	
	public static boolean validateName(String name) {
		if (name == null) {
			return false;
		}
		if (name.isEmpty()) {
			return false;
		}
		if (name.length() > 16) {
			return false;
		}
		if (name.matches("^\\w+$")) {
			return true;
		}
		return false;
	}
	
	public static Vector getVector(String dir) {
		switch (dir) {
		case "north":
			return new Vector(0,0,-1);
		case "east":
			return new Vector(1,0,0);
		case "west":
			return new Vector(-1,0,0);
		case "south":
			return new Vector(0,0,1);
		}
		return null;
	}
	
	public static BlockFace getBlockFace(String dir) {
		switch (dir) {
		case "north":
			return BlockFace.NORTH;
		case "east":
			return BlockFace.EAST;
		case "west":
			return BlockFace.WEST;
		case "south":
			return BlockFace.SOUTH;
		}
		return BlockFace.NORTH;
	}
	
	public static boolean isValidDirection(String dir) {
		dir = dir.toLowerCase();
		return dir.equals("north")  || dir.equals("east") || dir.equals("south") || dir.equals("west");
	}
	
	public static boolean isCartOverBlock(Minecart cart, Material material) {
		return cart.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == material;
	}
	
	public static boolean hasPassenger(Minecart cart) {
		Entity passenger = cart.getPassenger();
		if (passenger == null || !(passenger instanceof LivingEntity)) {
			return false;
		}
		return true;
	}
	
	public static boolean hasPlayer(Minecart cart) {
		Entity passenger = cart.getPassenger();
		if (passenger == null || !(passenger instanceof Player)) {
			return false;
		}
		return true;
	}
	
	public static boolean isOnRails(Minecart cart) {
		Block rail = cart.getLocation().getBlock();
		return 	isRails(rail);
	}
	
	public static boolean isRails(Block rail) {
		return 	rail.getType() == Material.ACTIVATOR_RAIL
				|| rail.getType() == Material.DETECTOR_RAIL
				|| rail.getType() == Material.POWERED_RAIL
				|| rail.getType() == Material.RAILS;
	}
	
	public static void stopCart(Minecart cart) {
		cart.setVelocity(new Vector(0,0,0));
	}
	
	public static void pushCart(Minecart cart, String dir) {
		Vector v = GoXUtils.getVector(dir).multiply(cart.getMaxSpeed()*0.7);
		cart.setVelocity(v);
	}
	
	public static void turnRail(Minecart cart, String dir) {
		Block rail = cart.getLocation().getBlock();
		BlockState state = rail.getState();
		Rails railsState = (Rails) state.getData();
		if (!isAngle(rail, railsState) && dir != null) {
			railsState.setDirection(GoXUtils.getBlockFace(dir), false);
			state.setData(railsState);
			state.update();
		}
	}
	
	public static boolean isAngle(Block rail, Rails state) {
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
	
	public static void setMinecartDirection(Minecart cart, String dir) {
		Vector velocity = cart.getVelocity();
		double speed = velocity.length();
		Vector newDirection = GoXUtils.getVector(dir).multiply(speed);
		cart.setVelocity(newDirection);
	}
	
	public static String repathRoutine(GoXPlayer gp, GoXNode currentNode) {
		gp.getPlayer().sendMessage(ChatColor.YELLOW+GoXChat.chat("searching path"));
		
		GoXPath path = GoXMap.FindPath(currentNode.getId(), gp.getDestination());
		
		if (path == null || path.IsEmpty()) {
			gp.reset();
			gp.getPlayer().sendMessage(ChatColor.RED+GoXChat.chat("path not found"));
			return null;
		}
		
		gp.getPlayer().sendMessage(ChatColor.GREEN+GoXChat.chat("path found"));
		
		String startDirection = path.Pop();
		gp.setPath(path);
		gp.setNext(startDirection);
		gp.setExpected(currentNode.getId());
		return startDirection;
	}
}
