package com.radiantai.gox.structures;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;

public class GoXDispenser {
	
	private Block dispenser;
	
	public GoXDispenser(Block dispenser) {
		this.dispenser = dispenser;
	}
	
	public Minecart placeCart() {
		Minecart cart = null;
		Directional data = (Directional) dispenser.getState().getData();
		Block onto = dispenser.getRelative(data.getFacing());
		cart = GoXCart.createCart(onto.getLocation().clone().add(new Vector(0.5,0,0.5)));
		return cart;
	}
	
	public boolean isFacingRails() {
		Directional data = (Directional) dispenser.getState().getData();
		Block onto = dispenser.getRelative(data.getFacing());
		return GoXRail.isRails(onto);
	}
	
	public void scheduleRemoveCartFromInventory(GoX plugin) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run()
            {
            	Inventory inventory = ((Dispenser) dispenser.getState()).getInventory();
        		int cartPos = inventory.first(new ItemStack(Material.MINECART));
        		if (cartPos >= 0) {
        			inventory.setItem(cartPos, null);
        		}
            }
        }, 1);
	}
}
