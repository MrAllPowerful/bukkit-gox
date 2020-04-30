package com.radiantai.gox.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GoXCartContainerOwner implements IGoXCartOwner {
	
	private Location containerLocation;
	
	public GoXCartContainerOwner(Location containerLocation) {
		this.containerLocation = containerLocation;
	}

	@Override
	public void returnCart() {
		Block block = containerLocation.getBlock();
		if (block != null) {
			
			Inventory inventory = null;
			
			if (block.getType() == Material.DISPENSER) {
				inventory = ((Dispenser) block.getState()).getInventory();
			}
			else if (block.getType() == Material.CHEST) {
				inventory = ((Chest) block.getState()).getInventory();
			}
			
			if (inventory != null && inventory.firstEmpty() != -1) {
				inventory.addItem(new ItemStack(Material.MINECART));
			}
			else {
				containerLocation.getWorld().dropItemNaturally(containerLocation, new ItemStack(Material.MINECART));
			}
		}
		else {
			containerLocation.getWorld().dropItemNaturally(containerLocation, new ItemStack(Material.MINECART));
		}
	}

	@Override
	public Location getLocation() {
		return containerLocation;
	}

}
