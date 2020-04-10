package com.radiantai.gox.structures;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Rails;

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
				|| rail.getType() == Material.RAILS;
	}
	
	public boolean isAngle() {
		if (!isRails(rail)) {
			return false;
		}
		BlockState state = rail.getState();
		Rails railsState = (Rails) state.getData();
		if (railsState.isCurve()) {
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
			setDirection(dir, false);
		}
	}
	
	public void setDirection(GoXDirection dir, boolean curved) {
		BlockState state = rail.getState();
		Rails railsState = (Rails) state.getData();
		railsState.setDirection(dir.getBlockFace(), curved);
		state.setData(railsState);
		state.update();
	}
}
