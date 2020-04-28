package com.radiantai.gox.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXDirection;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXStation;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXPathMovement implements Listener {
	
	private GoX plugin;
	
	public GoXPathMovement(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMinecartMove(VehicleMoveEvent e){
		
		if (!shouldTrack(e)) {
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		GoXCart gc = new GoXCart(cart, plugin);
		Player player = gc.getPlayer();
		GoXPlayer gp = new GoXPlayer(player, plugin);
		
		if (gp.getDestination() == null) {
			return;
		}
		
		GoXNode node = GoXMap.GetNode(cart.getLocation());
		
		if (hasArrived(gp)) {
			gp.reset();
			player.sendMessage(ChatColor.YELLOW+GoXChat.chat("arrived")+":");
			gc.stopCart();
			if (node instanceof GoXStation) {
				Location drop = ((GoXStation) node).getDropPoint();
				if (drop != null) {
					player.leaveVehicle();
					player.teleport(drop);
					gc.destroyAndReturn();
				}
			}
			if (node instanceof GoXStation) {
				GoXChat.fancyStationCompact(player,(GoXStation) node);
			}
			return;
		}
		
		if (gc.isCartOverBlock(plugin.getStationBlock()) ||
				gc.isCartOverBlock(plugin.getNodeBlock())) {
			
			if (node == null) {
				return;
			}
			
			GoXDirection nextDir = gp.getNext();
			
			if(needRepath(gp, node)) {
				gc.stopCart();
				gp.resetPath();
				
				GoXDirection startDirection = GoXUtils.repathRoutine(gp, node);
				
				if (startDirection != null) {
					gc.turnRail(startDirection);
					gc.pushCart(startDirection);
				}
				return;
			}
			
			if (nextDir == null) {
				return;
			}
			
			gc.turnRail(nextDir);
			gc.setMinecartDirection(nextDir);
			
			gp.setNext(null);
			gp.setExpected(node.getLink(nextDir) != null ? node.getLink(nextDir).getId() : null);
		}	
		else {
			GoXDirection dirOther = gp.getNext();
			if (dirOther == null) {
				gp.setNext(gp.popPath());
				return;
			}
			return;
		}
	}
	
	private boolean shouldTrack(VehicleMoveEvent e) {
		if(!(e.getVehicle() instanceof Minecart)){
			return false;
		}
		Minecart cart = (Minecart)e.getVehicle();
		GoXCart gc = new GoXCart(cart, plugin);
		if (!gc.isOnRails() || gc.getPlayer() == null) {
			return false;
		}
		return true;
	}
	
	private boolean hasArrived(GoXPlayer gp) {
		
		Block block = gp.getPlayer().getVehicle().getLocation().getBlock().getRelative(BlockFace.DOWN);
		Location nodeLocation = block.getLocation();
		
		GoXNode node = GoXMap.GetNode(nodeLocation);
		String destination = gp.getDestination();
		
		if (destination != null) {
			if (node != null && node.getId().equals(destination)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean needRepath(GoXPlayer gp, GoXNode currNode) {
		String expected = gp.getExpected();
		
		if (gp.getPath() == null) {
			return true;
		}
		
		if (!currNode.getId().equals(expected) && gp.getNext() != null) {
			gp.getPlayer().sendMessage(ChatColor.GRAY+GoXChat.chat("repath"));
			return true;
		}
		
		return false;
	}
}
