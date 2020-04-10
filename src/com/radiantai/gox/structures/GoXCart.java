package com.radiantai.gox.structures;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXDirection;

public class GoXCart {
	private Minecart cart;
	private GoX plugin;
	public GoXCart(Minecart cart, GoX plugin) {
		this.cart = cart;
		this.plugin = plugin;
	}
	
	public Minecart getCart() {
		return cart;
	}

	public boolean hasPassenger() {
		Entity passenger = cart.getPassenger();
		if (passenger == null || !(passenger instanceof LivingEntity)) {
			return false;
		}
		return true;
	}
	
	public boolean hasPlayer() {
		Entity passenger = cart.getPassenger();
		if (passenger == null || !(passenger instanceof Player)) {
			return false;
		}
		return true;
	}
	
	public boolean isOnRails() {
		Block rail = cart.getLocation().getBlock();
		return 	GoXRail.isRails(rail);
	}
	
	public void stopCart() {
		cart.setVelocity(new Vector(0,0,0));
	}
	
	public void pushCart(GoXDirection dir) {
		Vector v = dir.getVector().multiply(cart.getMaxSpeed()*0.7);
		cart.setVelocity(v);
	}
	
	public void turnRail(GoXDirection dir) {
		Block rail = cart.getLocation().getBlock();
		if (!GoXRail.isRails(rail)) {
			return;
		}
		GoXRail gr = new GoXRail(rail, plugin);
		gr.turnRail(dir);
	}
	
	public void setMinecartDirection(GoXDirection dir) {
		Vector velocity = cart.getVelocity();
		double speed = velocity.length();
		Vector newDirection = dir.getVector().multiply(speed);
		cart.setVelocity(newDirection);
	}
	
	public boolean isCartOverBlock(Material material) {
		return cart.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == material;
	}
}
