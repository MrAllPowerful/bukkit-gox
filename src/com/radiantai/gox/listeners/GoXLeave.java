package com.radiantai.gox.listeners;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXPlayer;

public class GoXLeave implements Listener {
	
	private GoX plugin;
	
	public GoXLeave(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMinecartLeave(VehicleExitEvent e){
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		GoXCart gc = new GoXCart(cart, plugin);
		
		if (gc.getPlayer() == null) {
			return;
		}
		
		Player player = (Player) e.getExited();
		
		GoXPlayer gp = new GoXPlayer(player, plugin);
		
		if (gp.getDestination() != null) {
			gc.destroyAndReturn();
			gp.resetPath();
		}
		else {
			gc.setTicksWhenleft(cart.getTicksLived());
		}
		
	}
}
