package com.radiantai.gox.structures;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXDirection;

public class GoXRail {
	private Block rail;
	private GoX plugin;
	
	public GoXRail(Block rail, GoX plugin) {
		this.rail = rail;
		this.plugin = plugin;
	}
	
	public Block getRail() {
		return rail;
	}

	public static boolean isRails(Block rail) {
		return 	rail.getType() == Material.ACTIVATOR_RAIL
				|| rail.getType() == Material.DETECTOR_RAIL
				|| rail.getType() == Material.POWERED_RAIL
				|| rail.getType() == Material.RAIL;
	}
	
	public boolean isAngle() {
		if (!isRails(rail)) {
			return false;
		}
		BlockData state = rail.getBlockData();
		Rail railsState = (Rail) state;
		if (isCurved(railsState)) {
			boolean north, east, south, west;
			north = isRails(rail.getRelative(BlockFace.NORTH));
			east = isRails(rail.getRelative(BlockFace.EAST));
			south = isRails(rail.getRelative(BlockFace.SOUTH));
			west = isRails(rail.getRelative(BlockFace.WEST));
			if (north && east && !west && !south) return true;
			if (north && west && !east && !south) return true;
			if (south && east && !west && !north) return true;
			if (south && west && !east && !north) return true;
		}
		return false;
	}
	
	public void turnRail(GoXDirection dir) {
		GoXRail gr = new GoXRail(rail, plugin);
		if (!gr.isAngle() && dir != null) {
			setDirection(dir);
		}
	}
	
	public void setDirection(GoXDirection dir) {
		BlockData state = rail.getBlockData();
		Rail railsState = (Rail) state;
		Rail.Shape shape = 
				dir.getDir() == GoXDirection.Direction.NORTH || dir.getDir() == GoXDirection.Direction.SOUTH ?
						Rail.Shape.NORTH_SOUTH : Rail.Shape.EAST_WEST;
		railsState.setShape(shape);
		rail.setBlockData(railsState);
	}
	
	private boolean isCurved(Rail state) {
		Rail.Shape shape = state.getShape();
		return shape == Rail.Shape.SOUTH_EAST || shape == Rail.Shape.NORTH_EAST || shape == Rail.Shape.NORTH_WEST || shape == Rail.Shape.SOUTH_WEST;
	}
}
