package com.radiantai.gox.pathfinding;

import org.bukkit.ChatColor;
import org.bukkit.Location;

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
	
	public static GoXDirection repathRoutine(GoXPlayer gp, GoXNode currentNode) {
		gp.getPlayer().sendMessage(ChatColor.YELLOW+GoXChat.chat("searching path"));
		
		GoXPath path = GoXMap.FindPath(currentNode.getId(), gp.getDestination());
		
		if (path == null || path.IsEmpty()) {
			gp.reset();
			gp.getPlayer().sendMessage(ChatColor.RED+GoXChat.chat("path not found"));
			return null;
		}
		
		gp.getPlayer().sendMessage(ChatColor.GREEN+GoXChat.chat("path found"));
		GoXChat.estimatedTime(gp.getPlayer(), path);
		
		GoXDirection startDirection = path.Pop();
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
}
