package com.radiantai.gox.listeners;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.IGoXCartOwner;

public class GoXCartDestroy implements Listener {
	
	private GoX plugin;
	
	public GoXCartDestroy(GoX plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void oncartDestroy(VehicleDestroyEvent e) {
		if (e.getVehicle() instanceof RideableMinecart) {
			GoXCart gc = new GoXCart((Minecart) e.getVehicle(), plugin);
			IGoXCartOwner owner = gc.getOwner();
			if (owner != null) {
				owner.returnCart();
				e.setCancelled(true);
				gc.remove();
			}
		}
	}
}
