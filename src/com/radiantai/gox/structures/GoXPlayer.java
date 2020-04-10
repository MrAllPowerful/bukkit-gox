package com.radiantai.gox.structures;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXDirection;
import com.radiantai.gox.pathfinding.GoXPath;

public class GoXPlayer {
	
	private Player player;
	private GoX plugin;
	
	public GoXPlayer(Player player, GoX plugin) {
		this.player = player;
		this.plugin = plugin;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public GoX getPlugin() {
		return plugin;
	}

	public void setPlugin(GoX plugin) {
		this.plugin = plugin;
	}

	public void setNext(GoXDirection dir) {
		if (dir==null) {
			if (player.hasMetadata("go_next")) {
				player.removeMetadata("go_next", plugin);
			}
		}
		else {
			player.setMetadata("go_next", new FixedMetadataValue(plugin, dir));
		}
	}
	
	public void setAddstation(Location location) {
		if (location==null) {
			if (player.hasMetadata("go_add_station")) {
				player.removeMetadata("go_add_station", plugin);
			}
		}
		else {
			player.setMetadata("go_add_station", new FixedMetadataValue(plugin, location));
		}
	}
	
	public void setAddNode(Location location) {
		if (location==null) {
			if (player.hasMetadata("go_add")) {
				player.removeMetadata("go_add", plugin);
			}
		}
		else {
			player.setMetadata("go_add", new FixedMetadataValue(plugin, location));
		}
	}
	
	public void setExpected(String node) {
		if (node==null) {
			if (player.hasMetadata("go_exp")) {
				player.removeMetadata("go_exp", plugin);
			}
		}
		else {
			player.setMetadata("go_exp", new FixedMetadataValue(plugin, node));
		}
	}
	
	public void setDestination(String destination) {
		if (destination==null) {
			if (player.hasMetadata("go_destination")) {
				player.removeMetadata("go_destination", plugin);
			}
		}
		else {
			player.setMetadata("go_destination", new FixedMetadataValue(plugin, destination));
		}
	}

	public void setPath(GoXPath path) {
		if (path==null) {
			if (player.hasMetadata("go_path")) {
				player.removeMetadata("go_path", plugin);
			}
		}
		else {
			player.setMetadata("go_path", new FixedMetadataValue(plugin, path));
		}
	}
	
	public Location getAddstation() {
		Location result = null;
		if (player.hasMetadata("go_add_station")) {
			result = (Location) player.getMetadata("go_add_station").get(0).value();
			if (result == null) {
				player.removeMetadata("go_add_station", plugin);
			}
		}
		return result;
	}
	
	public Location getAddNode() {
		Location result = null;
		if (player.hasMetadata("go_add")) {
			result = (Location) player.getMetadata("go_add").get(0).value();
			if (result == null) {
				player.removeMetadata("go_add", plugin);
			}
		}
		return result;
	}
	
	public GoXDirection getNext() {
		GoXDirection result = null;
		if (player.hasMetadata("go_next")) {
			result = (GoXDirection) player.getMetadata("go_next").get(0).value();
			if (result == null) {
				player.removeMetadata("go_next", plugin);
			}
		}
		return result;
	}
	
	public String getExpected() {
		String result = null;
		if (player.hasMetadata("go_exp")) {
			result = player.getMetadata("go_exp").get(0).asString();
			if (result == null) {
				player.removeMetadata("go_exp", plugin);
			}
		}
		return result;
	}
	
	public String getDestination() {
		String result = null;
		if (player.hasMetadata("go_destination")) {
			result = player.getMetadata("go_destination").get(0).asString();
			if (result == null) {
				player.removeMetadata("go_destination", plugin);
			}
		}
		return result;
	}
	
	public GoXPath getPath() {
		GoXPath result = null;
		if (player.hasMetadata("go_path")) {
			result = (GoXPath) player.getMetadata("go_path").get(0).value();
			if (result == null) {
				player.removeMetadata("go_path", plugin);
			}
		}
		return result;
	}
	
	public GoXDirection popPath() {
		GoXDirection result = null;
		if (player.hasMetadata("go_path")) {
			GoXPath path = (GoXPath) player.getMetadata("go_path").get(0).value();
			if (path == null || path.IsEmpty()) {
				player.removeMetadata("go_path", plugin);
			}
			else {
				result = path.Pop();
			}
		}
		return result;
	}
	
	public GoXDirection peekPath() {
		GoXDirection result = null;
		if (player.hasMetadata("go_path")) {
			GoXPath path = (GoXPath) player.getMetadata("go_path").get(0).value();
			if (path == null) {
				player.removeMetadata("go_path", plugin);
			}
			else {
				result = path.Peek();
			}
		}
		return result;
	}
	
	public void reset() {
		setNext(null);
		setDestination(null);
		setPath(null);
		setExpected(null);
	}
	
	public void resetPath() {
		setNext(null);
		setPath(null);
		setExpected(null);
	}
	
	public void resetAdd() {
		setAddNode(null);
		setAddstation(null);
	}
	
	public String getPlayerDirection() {
		double rot = player.getLocation().getYaw();
		if (rot < 0) {
			rot = rot+360;
		}
		if (0 <= rot && rot < 22.5) {
            return "south";
        } else if (22.5 <= rot && rot < 67.5) {
            return "southwest";
        } else if (67.5 <= rot && rot < 112.5) {
            return "west";
        } else if (112.5 <= rot && rot < 157.5) {
            return "northwest";
        } else if (157.5 <= rot && rot < 202.5) {
            return "north";
        } else if (202.5 <= rot && rot < 247.5) {
            return "northeast";
        } else if (247.5 <= rot && rot < 292.5) {
            return "east";
        } else if (292.5 <= rot && rot < 337.5) {
            return "southeast";
        } else if (337.5 <= rot && rot < 360.0) {
            return "south";
        } else {
            return "undefined";
        }
	}
}
