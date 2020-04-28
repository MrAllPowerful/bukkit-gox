package com.radiantai.gox.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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

	public Player getPlayer() {
		Entity passenger = cart.getPassenger();
		if (passenger == null || !(passenger instanceof Player)) {
			return null;
		}
		return (Player) passenger;
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
	
	public void remove() {
		cart.remove();
	}
	
	public void setTicksWhenleft(int ticks) {
		if (ticks < 0) {
			if (cart.hasMetadata("go_tickswhenleft")) {
				cart.removeMetadata("go_tickswhenleft", plugin);
			}
		}
		else {
			cart.setMetadata("go_tickswhenleft", new FixedMetadataValue(plugin, ticks));
		}
	}
	
	public int getTicksWhenleft() {
		int result = -1;
		if (cart.hasMetadata("go_tickswhenleft")) {
			result = cart.getMetadata("go_tickswhenleft").get(0).asInt();
			if (result < 0) {
				cart.removeMetadata("go_tickswhenleft", plugin);
			}
		}
		return result;
	}
	
	public void setOwner(IGoXCartOwner owner) {
		if (owner==null) {
			if (cart.hasMetadata("go_playerowner")) {
				cart.removeMetadata("go_destination", plugin);
			}
		}
		else {
			cart.setMetadata("go_destination", new FixedMetadataValue(plugin, owner));
		}
	}
	
	public IGoXCartOwner getOwner() {
		IGoXCartOwner result = null;
		if (cart.hasMetadata("go_destination")) {
			result = (IGoXCartOwner) cart.getMetadata("go_destination").get(0).value();
			if (result == null) {
				cart.removeMetadata("go_destination", plugin);
			}
		}
		return result;
	}
	
	public void destroyAndReturn() {
		IGoXCartOwner owner = getOwner();
		if (owner != null) {
			owner.returnCart();
		}
		remove();
	}
	
	public static Minecart createCart(Location loc) {
		Minecart cart = null;
		cart = loc.getWorld().spawn(loc, Minecart.class);
		return cart;
	}
}
