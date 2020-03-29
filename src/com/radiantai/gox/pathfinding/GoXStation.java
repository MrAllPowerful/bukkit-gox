package com.radiantai.gox.pathfinding;

import org.bukkit.Location;

import com.radiantai.gox.chat.GoXChat;

public class GoXStation extends GoXNode implements Comparable<GoXStation> {
	private String name;
	private Location dropPoint;
	
	public GoXStation(String name, Location location) {
		super(location);
		this.name = name;
		this.dropPoint = null;
	}
	
	public GoXStation(String id, Location location, GoXNode north, GoXNode east, GoXNode south, GoXNode west, String name, String forceDirection, Location dropPoint) {
		super(id, location, north, east, south, west, forceDirection);
		this.name = name;
		this.dropPoint = dropPoint;
	}

	public String GetName() {
		return name;
	}
	
	public void SetName(String name) {
		this.name = name;
	}
	
	public Location getDropPoint() {
		return dropPoint;
	}

	public void setDropPoint(Location dropPoint) throws Exception {
		if (!super.location.getWorld().getName().equals(dropPoint.getWorld().getName())) {
			throw new Exception(GoXChat.chat("drop same world"));
		}
		if (dropPoint.distance(super.getLocation()) > 8) {
			throw new Exception(GoXChat.chat("drop distance")+" "+8);
		}
		this.dropPoint = dropPoint;
	}

	public String toString() {
		String norths = north == null ? "" : north.getId();
		String easts = east == null ? "" : east.getId();
		String souths = south == null ? "" : south.getId();
		String wests = west == null ? "" : west.getId();
		return "STATION> Name: "+name
				+" Id: "+id+" X: "+ location.getBlockX() + " Y: " + location.getBlockY() + " Z: "+location.getBlockZ()+" North: "
				+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}

	@Override
	public int compareTo(GoXStation o) {
		return this.GetName().compareTo(o.GetName());
	}
}
