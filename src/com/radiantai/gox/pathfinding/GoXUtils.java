package com.radiantai.gox.pathfinding;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXUtils {
	
	public static String GenerateId(int length) {
		
		String sample = "0123456789abcdefghijklmnopqrstuvxyz"; 
		String s = ""; 
		
		for (int i = 0; i < length; i++) { 
			int index = (int)(sample.length()*Math.random()); 
			s += sample.charAt(index); 
		}
		
		return s;
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
		dir = dir.toLowerCase();
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
		dir = dir.toLowerCase();
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
		dir = dir.toLowerCase();
		Vector v = GoXUtils.getVector(dir).multiply(cart.getMaxSpeed()*0.7);
		cart.setVelocity(v);
	}
	
	public static void turnRail(Minecart cart, String dir) {
		dir = dir.toLowerCase();
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
		dir = dir.toLowerCase();
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
	
	public static Location floorLocation(Location location) {
		Location newLoc = location.clone();
		newLoc.setX(location.getBlockX());
		newLoc.setY(location.getBlockY());
		newLoc.setZ(location.getBlockZ());
		return newLoc;
	}
	
	public static String getPlayerDirection(Player p) {
		double rot = p.getLocation().getYaw();
		if (0 <= rot && rot < 22.5) {
            return "north";
        } else if (22.5 <= rot && rot < 67.5) {
            return "northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "east";
        } else if (112.5 <= rot && rot < 157.5) {
            return "southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "south";
        } else if (202.5 <= rot && rot < 247.5) {
            return "southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "west";
        } else if (292.5 <= rot && rot < 337.5) {
            return "northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "north";
        } else {
            return "undefined";
        }
	}
}
