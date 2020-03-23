package com.radiantai.gox.structures;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXPath;

public class GoXPlayer {
	
	private Player player;
	private GoX plugin;
	
	public GoXPlayer(Player player, GoX plugin) {
		this.player = player;
		this.plugin = plugin;
	}
	
	public void setNext(String dir) {
		if (dir==null) {
			if (player.hasMetadata("go_next")) {
				player.removeMetadata("go_next", plugin);
			}
		}
		else {
			player.setMetadata("go_next", new FixedMetadataValue(plugin, dir));
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
	
	public void setRepath(boolean value) {
		if (value==false) {
			if (player.hasMetadata("go_repath")) {
				player.removeMetadata("go_repath", plugin);
			}
		}
		else {
			player.setMetadata("go_repath", new FixedMetadataValue(plugin, true));
		}
	}
	
	public String getNext() {
		String result = null;
		if (player.hasMetadata("go_next")) {
			result = player.getMetadata("go_next").get(0).asString();
			if (result == null) {
				player.removeMetadata("go_next", plugin);
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
		if (player.hasMetadata("go_destination")) {
			result = (GoXPath) player.getMetadata("go_destination").get(0).value();
			if (result == null) {
				player.removeMetadata("go_destination", plugin);
			}
			else if (result.IsEmpty()) {
				player.removeMetadata("go_destination", plugin);
			}
		}
		return result;
	}
	
	public String popPath() {
		String result = null;
		if (player.hasMetadata("go_path")) {
			GoXPath path = (GoXPath) player.getMetadata("go_path").get(0).value();
			if (path == null || path.IsEmpty()) {
				player.removeMetadata("go_path", plugin);
			}
			else {
				result = path.Pop();
				if (path.IsEmpty()) {
					player.removeMetadata("go_path", plugin);
				}
			}
		}
		return result;
	}
	
	public String peekPath() {
		String result = null;
		if (player.hasMetadata("go_path")) {
			GoXPath path = (GoXPath) player.getMetadata("go_path").get(0).value();
			if (path == null || path.IsEmpty()) {
				player.removeMetadata("go_path", plugin);
			}
			else {
				result = path.Peek();
			}
		}
		return result;
	}
	
	public boolean getRepath() {
		boolean result = false;
		if (player.hasMetadata("go_repath")) {
			result = true;
		}
		return result;
	}
	
	public void reset() {
		setNext(null);
		setDestination(null);
		setPath(null);
		setRepath(false);
	}
	
	public void resetPath() {
		setNext(null);
		setPath(null);
		setRepath(false);
	}
}
