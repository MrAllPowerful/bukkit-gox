package com.radiantai.gox.pathfinding;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class GoXNode {
	protected String id;
	protected Location location;
	protected GoXNode north;
	protected GoXNode east;
	protected GoXNode south;
	protected GoXNode west;
	protected boolean visited;
	protected GoXNode prev;
	protected String fromPrev;
	
	public GoXNode(Location location) {
		id = GoXUtils.GenerateId(8);
		this.location = GoXUtils.floorLocation(location);
	}
	
	public GoXNode(String id, Location location, GoXNode north, GoXNode east, GoXNode south, GoXNode west) {
		this.id = id;
		this.location = GoXUtils.floorLocation(location);
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.visited = false;
		this.prev = null;
		this.fromPrev = null;
	}

	protected GoXNode clone() {
		return new GoXNode(id, location, north, east, south, west);
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

	public String getFromPrev() {
		return fromPrev;
	}

	public GoXNode setFromPrev(String fromPrev) {
		this.fromPrev = fromPrev;
		return this;
	}
	
	public GoXNode getPrev() {
		return prev;
	}

	public GoXNode setPrev(GoXNode prev) {
		this.prev = prev;
		return this;
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
	
	public void setLink(String dir, GoXNode node) {
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

	public String toString() {
		String norths = north == null ? "" : north.getId();
		String easts = east == null ? "" : east.getId();
		String souths = south == null ? "" : south.getId();
		String wests = west == null ? "" : west.getId();
		return "Node> Id: "+id+" X: "+location.getBlockX()+ " Y: " + location.getBlockY() +" Z: "+location.getBlockZ()+" North: "+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}
}
