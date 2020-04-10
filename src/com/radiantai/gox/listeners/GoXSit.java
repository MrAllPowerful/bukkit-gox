package com.radiantai.gox.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXDirection;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXPlayer;
import com.radiantai.gox.structures.GoXRail;

public class GoXSit implements Listener {
	private GoX plugin;
	
	public GoXSit(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCartSit(VehicleEnterEvent e){
		
		if (!(e.getVehicle() instanceof Minecart)) {
			return;
		}
		if (!(e.getEntered() instanceof Player)) {
			return;
		}
		
		Minecart cart = (Minecart) e.getVehicle();
		Player player = (Player) e.getEntered();
		
		cart.setMaxSpeed(0.4*plugin.getCartMaxSpeed());
		
		GoXPlayer gp = new GoXPlayer(player, plugin);
		GoXCart gc = new GoXCart(cart, plugin);
		String destination = gp.getDestination();
		
		if (destination == null) {
			return;
		}
		
		Block block = cart.getLocation().getBlock();
		if (!GoXRail.isRails(block)) {
			return;
		}
		
		if (!gc.isCartOverBlock(plugin.getStationBlock()) &&
				!gc.isCartOverBlock(plugin.getNodeBlock())) {
			return;
		}
		
		GoXNode node = GoXMap.GetNode(cart.getLocation());
		if (node == null) {
			return;
		}
		
		GoXDirection startDirection = GoXUtils.repathRoutine(gp, node);
		
		if (startDirection != null) {
			gc.turnRail(startDirection);
			gc.pushCart(startDirection);
		}
		
	}
}
