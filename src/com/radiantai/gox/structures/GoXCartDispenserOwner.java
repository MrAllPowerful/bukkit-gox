package com.radiantai.gox.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GoXCartDispenserOwner implements IGoXCartOwner {
	
	private Location dispenserLocation;
	
	public GoXCartDispenserOwner(Location dispenserLocation) {
		this.dispenserLocation = dispenserLocation;
	}

	@Override
	public void returnCart() {
		Block block = dispenserLocation.getBlock();
		if (block != null && block.getType() == Material.DISPENSER) {
			Inventory inventory = ((Dispenser) block.getState()).getInventory();
			if (inventory.firstEmpty() != -1) {
				inventory.addItem(new ItemStack(Material.MINECART));
			}
			else {
				dispenserLocation.getWorld().dropItemNaturally(dispenserLocation, new ItemStack(Material.MINECART));
			}
		}
		else {
			dispenserLocation.getWorld().dropItemNaturally(dispenserLocation, new ItemStack(Material.MINECART));
		}
	}

	@Override
	public Location getLocation() {
		return dispenserLocation;
	}

}
