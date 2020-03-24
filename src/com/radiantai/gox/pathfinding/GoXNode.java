package com.radiantai.gox.pathfinding;

import java.util.List;

import org.bukkit.ChatColor;

public class GoXNode {
	protected String id;
	protected int x;
	protected int y;
	protected int z;
	protected GoXNode north;
	protected GoXNode east;
	protected GoXNode south;
	protected GoXNode west;
	protected boolean visited;
	protected GoXNode prev;
	protected String fromPrev;
	
	public GoXNode(int x, int y, int z) {
		id = GoXUtils.GenerateId(16);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public GoXNode(String id, int x, int y, int z, GoXNode north, GoXNode east, GoXNode south, GoXNode west) {
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

	protected GoXNode clone() {
		return new GoXNode(id, x, y, z, north, east, south, west);
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

	public String toString() {
		String norths = north == null ? "" : north.getId();
		String easts = east == null ? "" : east.getId();
		String souths = south == null ? "" : south.getId();
		String wests = west == null ? "" : west.getId();
		return "Node> Id: "+id+" X: "+x+ " Y: " + y +" Z: "+z+" North: "+norths+" East: "+easts+" South: "+souths+" West: "+wests;
	}
}
