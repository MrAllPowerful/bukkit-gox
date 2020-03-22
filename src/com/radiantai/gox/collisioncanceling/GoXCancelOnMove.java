package com.radiantai.gox.collisioncanceling;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.Utils;
import com.radiantai.gox.structures.RollingQueue;

public class GoXCancelOnMove implements Listener {
	
	private GoX plugin;
	private Logger logger;
	
	public GoXCancelOnMove(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}
	
	@EventHandler()
	public void onMinecartMove(VehicleMoveEvent e){
		
		if(!(e.getVehicle() instanceof Minecart)){
			return;
		}
		
		Minecart cart = (Minecart)e.getVehicle();
		
		if (!Utils.isOnRails(cart) || !Utils.hasPassenger(cart)) {
			return;
		}
		
//		Vector v = cart.getVelocity();
//		 
//		
//		if (cart.hasMetadata("gox_velstack")) {
//			RollingQueue<Vector> q = (RollingQueue<Vector>) cart.getMetadata("gox_velstack").get(0).value();
//			if (!cart.hasMetadata("gox_col")) {
//				q.add(v);
//			}
//			//logger.info(q.toString());
//			cart.removeMetadata("gox_velstack", plugin);
//			cart.setMetadata("gox_velstack", new FixedMetadataValue(plugin, q));
//		}
//		else {
//			RollingQueue<Vector> q = new RollingQueue<Vector>(3);
//			q.add(v);
//			cart.setMetadata("gox_velstack", new FixedMetadataValue(plugin, q));
//		}
	}
}
