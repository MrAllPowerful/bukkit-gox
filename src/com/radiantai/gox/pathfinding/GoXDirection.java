package com.radiantai.gox.pathfinding;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.GoXException;

public class GoXDirection {
	private Direction dir;
	public enum Direction {
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	public GoXDirection(String dir) throws GoXException {
		if (dir == null) {
			throw new GoXException(GoXChat.chat("invalid direction")+"null");
		}
		else {
			dir = dir.toLowerCase();
			
			if (!isValidDirection(dir)) {
				throw new GoXException(GoXChat.chat("invalid direction")+dir);
			}
			
			switch (dir) {
			case "north":
				this.dir = Direction.NORTH;
				break;
			case "east":
				this.dir = Direction.EAST;
				break;
			case "south":
				this.dir = Direction.SOUTH;
				break;
			case "west":
				this.dir = Direction.WEST;
				break;
			default:
				this.dir = Direction.NORTH;
			}
		}
	}
	
	public GoXDirection(Direction dir) {
		this.dir = dir;
	}
	
	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public Vector getVector() {
		switch (dir) {
		case NORTH:
			return new Vector(0,0,-1);
		case EAST:
			return new Vector(1,0,0);
		case WEST:
			return new Vector(-1,0,0);
		case SOUTH:
			return new Vector(0,0,1);
		}
		return null;
	}
	
	public BlockFace getBlockFace() {
		switch (dir) {
		case NORTH:
			return BlockFace.NORTH;
		case EAST:
			return BlockFace.EAST;
		case WEST:
			return BlockFace.WEST;
		case SOUTH:
			return BlockFace.SOUTH;
		}
		return BlockFace.NORTH;
	}
	
	public static boolean isValidDirection(String dir) {
		dir = dir.toLowerCase();
		return dir.equals("north")  || dir.equals("east") || dir.equals("south") || dir.equals("west");
	}
	
	public String toString() {
		if (dir == null) {
			return "null";
		}
		return dir.name();
	}
	
	public boolean equals(GoXDirection other) {
		return dir == other.getDir();
	}
}
