package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.Utils;

public class GoXLeave implements Listener {
	
	private GoX plugin;
	private Logger logger;
	
	public GoXLeave(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler
	public void onMinecartLeave(VehicleExitEvent e){
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		
		if (!Utils.hasPassenger(cart)) {
			return;
		}
		
		if (!(cart.getPassenger() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getExited();
		
		Utils.resetPathMeta(player, plugin);
		
	}
}
