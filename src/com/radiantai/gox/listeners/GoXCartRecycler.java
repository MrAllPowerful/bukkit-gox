package com.radiantai.gox.listeners;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.structures.GoXCart;

	public class GoXCartRecycler implements Listener {
	
	private GoX plugin;
	
	public GoXCartRecycler(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMinecartUpdate(VehicleUpdateEvent e) {
		Vehicle v = e.getVehicle();
		if (v instanceof Minecart) {
			Minecart cart = (Minecart) v;
			GoXCart gc = new GoXCart(cart, plugin);
			
			if (gc.getPlayer() != null) {
				return;
			}
			
			if (gc.isCartOverBlock(plugin.getStationBlock()) || gc.isCartOverBlock(plugin.getNodeBlock())) {
				GoXNode node = GoXMap.GetNode(cart.getLocation());
				if (node != null) {
					int maxTicks = plugin.getCartTicksToLive();
					if (maxTicks > 0) {
						int ticksLived = cart.getTicksLived();
						int ticksAfterLeave = gc.getTicksWhenleft();
						if (ticksAfterLeave >= 0 && maxTicks<ticksLived-ticksAfterLeave) {
							gc.destroyAndReturn();
						}
						else if (ticksAfterLeave < 0 && ticksLived > maxTicks) {
							gc.destroyAndReturn();
						}
					}
				}
			}
			
		}
	}
}
