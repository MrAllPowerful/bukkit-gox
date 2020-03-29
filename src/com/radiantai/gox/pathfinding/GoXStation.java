package com.radiantai.gox.pathfinding;

import org.bukkit.Location;

public class GoXStation extends GoXNode {
	private String name;
	
	public GoXStation(String name, Location location) {
		super(location);
		this.name = name;
	}
	
	public GoXStation(String id, Location location, GoXNode north, GoXNode east, GoXNode south, GoXNode west, String name) {
		super(id, location, north, east, south, west);
		this.name = name;
	}

	public String GetName() {
		return name;
	}
	
	public void SetName(String name) {
		this.name = name;
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
}
