package com.radiantai.gox.collisioncanceling;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.RollingQueue;

public class GoXCollision implements Listener {
	
	private GoX plugin;
	private Logger logger;
	
	public GoXCollision(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler()
	public void onMinecartCollision(VehicleEntityCollisionEvent e){
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		
		if (!GoXUtils.hasPlayer(cart)) {
			return;
		}
		
//		Player player = (Player) cart.getPassenger();
//		
//		e.setCancelled(true);
//		e.setCollisionCancelled(true);
//		e.setPickupCancelled(true);
//		
//		Minecart cart = (Minecart)e.getVehicle();
//		
//		Vector velocity = null;
//		
//		if (cart.hasMetadata("gox_velstack")) {
//			
//			RollingQueue<Vector> q = (RollingQueue<Vector>) cart.getMetadata("gox_velstack").get(0).value();
//			velocity = q.get();
//			
//			Entity p = cart.getPassenger();
//			
//			cart.setMetadata("gox_col", new FixedMetadataValue(plugin, true));
//			
//			if (p instanceof LivingEntity) {
//				Location l = cart.getLocation().clone();
//				cart.remove();
//				Location newLoc = l.add(velocity.normalize().multiply(0.35));
//				Minecart newCart = (Minecart) l.getWorld().spawnEntity(newLoc, EntityType.MINECART);
//				newCart.setPassenger(p);
//				newCart.setVelocity(velocity);
//			}
//		}
		
		
		
	}
}
