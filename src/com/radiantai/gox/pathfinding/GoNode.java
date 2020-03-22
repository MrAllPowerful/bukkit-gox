package com.radiantai.gox.pathfinding;

import java.util.List;

import org.bukkit.ChatColor;

public class GoNode {
	protected String id;
	protected int x;
	protected int y;
	protected int z;
	protected GoNode north;
	protected GoNode east;
	protected GoNode south;
	protected GoNode west;
	protected boolean visited;
	protected GoNode prev;
	protected String fromPrev;
	
	public GoNode(int x, int y, int z) {
		id = Utils.GenerateId(8);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public GoNode(String id, int x, int y, int z, GoNode north, GoNode east, GoNode south, GoNode west) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.visited = false;
		this.prev = null;
		this.fromPrev = null;
	}

	protected GoNode clone() {
		return new GoNode(id, x, y, z, north, east, south, west);
	}

	public String getId(){
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getY() {
		return y;
	}

	public GoNode getNorth(){
		return north;
	}
	
	public GoNode getEast(){
		return east;
	}
	
	public GoNode getSouth(){
		return south;
	}
	
	public GoNode getWest(){
		return west;
	}
	
	public void SetNorth(GoNode north) {
		this.north = north;
	}

	public void SetEast(GoNode east) {
		this.east = east;
	}

	public void SetSouth(GoNode south) {
		this.south = south;
	}

	public void SetWest(GoNode west) {
		this.west = west;
	}

	public String getFromPrev() {
		return fromPrev;
	}

	public GoNode setFromPrev(String fromPrev) {
		this.fromPrev = fromPrev;
		return this;
	}
	
	public GoNode getPrev() {
		return prev;
	}

	public GoNode setPrev(GoNode prev) {
		this.prev = prev;
		return this;
	}

	public String toString() {
		String norths = north == null ? "" : north.getId();
		String easts = east == null ? "" : east.getId();
		String souths = south == null ? "" : south.getId();
		String wests = west == null ? "" : west.getId();
		return ChatColor.YELLOW+"Node>"+ChatColor.GREEN+" Id: "+id+" X: "+x+ " Y: " + y +" Z: "+z+" North: "+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}
}
