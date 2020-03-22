package com.radiantai.gox.pathfinding;

import org.bukkit.ChatColor;

public class GoStation extends GoNode {
	private String name;
	
	public GoStation(String name, int x, int y, int z) {
		super(x, y, z);
		this.name = name;
	}
	
	public GoStation(String id, int x, int y, int z, GoNode north, GoNode east, GoNode south, GoNode west, String name) {
		super(id, x, y, z, north, east, south, west);
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
		return ChatColor.YELLOW+"STATION>"+ChatColor.GREEN+" Name: "+name
				+" Id: "+id+" X: "+x + " Y: " + y + " Z: "+z+" North: "+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}
}
