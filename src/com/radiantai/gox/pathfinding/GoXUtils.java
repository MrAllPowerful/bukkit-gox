package com.radiantai.gox.pathfinding;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;

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
}
