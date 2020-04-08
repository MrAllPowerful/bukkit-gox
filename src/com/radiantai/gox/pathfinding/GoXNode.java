package com.radiantai.gox.pathfinding;

import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.radiantai.gox.chat.GoXChat;

public class GoXNode {
	protected String id;
	protected Location location;
	protected GoXNode north;
	protected GoXNode east;
	protected GoXNode south;
	protected GoXNode west;
	protected String forceDirection;
	
	public GoXNode(Location location) {
		id = GoXUtils.GenerateId(8);
		this.location = GoXUtils.floorLocation(location);
	}
	
	public GoXNode(String id, Location location, GoXNode north, GoXNode east, GoXNode south, GoXNode west, String forceDirection) {
		this.id = id;
		this.location = GoXUtils.floorLocation(location);
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.forceDirection = forceDirection;
	}

	protected GoXNode clone() {
		return new GoXNode(id, location, north, east, south, west, forceDirection);
	}

	public String getId(){
		return id;
	}
	
	public int getX() {
		return location.getBlockX();
	}
	
	public int getZ() {
		return location.getBlockZ();
	}
	
	public int getY() {
		return location.getBlockY();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getWorld() {
		return location.getWorld().getName();
	}

	public GoXNode getNorth(){
		return north;
	}
	
	public GoXNode getEast(){
		return east;
	}
	
	public GoXNode getSouth(){
		return south;
	}
	
	public GoXNode getWest(){
		return west;
	}
	
	public void SetNorth(GoXNode north) {
		this.north = north;
	}

	public void SetEast(GoXNode east) {
		this.east = east;
	}

	public void SetSouth(GoXNode south) {
		this.south = south;
	}

	public void SetWest(GoXNode west) {
		this.west = west;
	}

	public String getForceDirection() {
		return forceDirection;
	}

	public void setForceDirection(String forceDirection) {
		this.forceDirection = forceDirection;
	}
	
	public GoXNode getLink(String dir) {
		switch (dir) {
			case "north":
				return north;
			case "east":
				return east;
			case "south":
				return south;
			case "west":
				return west;
		}
		return null;
	}
	
	public void setLink(String dir, GoXNode node) throws Exception {
		if (node != null && id.equals(node.getId())) {
			throw new Exception(GoXChat.chat("to itself"));
		}
		switch (dir) {
			case "north":
				north = node;
				break;
			case "east":
				east = node;
				break;
			case "south":
				south = node;
				break;
			case "west":
				west = node;
				break;
		}
	}
	
	public void unlink(String id) {
		if (id != null) {
			if (north != null && north.getId().equals(id))
				north = null;
			if (east != null && east.getId().equals(id))
				east = null;
			if (south != null && south.getId().equals(id))
				south = null;
			if (west != null && west.getId().equals(id))
				west = null;
		}
	}
	
	public String isLinked(String id) {
		if (id != null) {
			if (north != null && north.getId().equals(id))
				return "north";
			if (east != null && east.getId().equals(id))
				return "east";
			if (south != null && south.getId().equals(id))
				return "south";
			if (west != null && west.getId().equals(id))
				return "west";
		}
		return null;
	}
	
	public int blockDistance(GoXNode other) {
		Location loc1 = this.getLocation();
		Location loc2 = other.getLocation();
		return Math.abs(loc1.getBlockX()-loc2.getBlockX()) + Math.abs(loc1.getBlockZ()-loc2.getBlockZ());
	}
	
	public int blockDistance(Location otherLocation) {
		Location loc = this.getLocation();
		return Math.abs(loc.getBlockX()-otherLocation.getBlockX()) + Math.abs(loc.getBlockZ()-otherLocation.getBlockZ());
	}

	public String toString() {
		String norths = north == null ? "" : north.getId();
		String easts = east == null ? "" : east.getId();
		String souths = south == null ? "" : south.getId();
		String wests = west == null ? "" : west.getId();
		return "Node> Id: "+id+" X: "+location.getBlockX()+ " Y: " + location.getBlockY() +" Z: "+location.getBlockZ()+" North: "+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}
	
	public static Comparator<GoXNode> distanceToLocationComparator(Location other) {
		return new Comparator<GoXNode>() {
			@Override
			public int compare(GoXNode node1, GoXNode node2) {
				if (!other.getWorld().getName().equals(node1.getWorld()) || !other.getWorld().getName().equals(node2.getWorld())) {
					return 0;
				}
				double distance1 = node1.getLocation().distance(other);
				double distance2 = node2.getLocation().distance(other);
				if (distance1 < distance2)
					return -1;
				else if (distance1 > distance2)
					return 1;
				else
					return 0;
			}
		};
	}
}
