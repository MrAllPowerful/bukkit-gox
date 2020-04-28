package com.radiantai.gox.structures;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;

public class GoXDispenser {
	
	private Dispenser dispenser;
	
	public GoXDispenser(Dispenser dispenser) {
		super();
		this.dispenser = dispenser;
	}
	
	public Minecart placeCart() {
		Minecart cart = null;
		DirectionalContainer data = (DirectionalContainer) dispenser.getData();
		Block onto = dispenser.getBlock().getRelative(data.getFacing());
		cart = GoXCart.createCart(onto.getLocation().add(new Vector(0.5,0.5,0.5)));
		return cart;
	}
	
	public boolean isFacingRails() {
		DirectionalContainer data = (DirectionalContainer) dispenser.getData();
		Block onto = dispenser.getBlock().getRelative(data.getFacing());
		return GoXRail.isRails(onto);
	}
	
	public void scheduleRemoveCartFromInventory(GoX plugin) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run()
            {
            	Inventory inventory = dispenser.getInventory();
        		int cartPos = inventory.first(new ItemStack(Material.MINECART));
        		if (cartPos >= 0) {
        			inventory.setItem(cartPos, null);
        		}
            }
        }, 1);
	}
}
