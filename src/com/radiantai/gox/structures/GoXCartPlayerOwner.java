package com.radiantai.gox.structures;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GoXCartPlayerOwner implements IGoXCartOwner {
	
	UUID playerId;
	
	public GoXCartPlayerOwner(UUID playerId) {
		this.playerId = playerId;
	}

	@Override
	public void returnCart() {
		Player player = Bukkit.getPlayer(playerId);
		if (player != null) {
			Inventory inventory = player.getInventory();
			if (inventory.firstEmpty() != -1) {
				inventory.addItem(new ItemStack(Material.MINECART));
			}
			else {
				getLocation().getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.MINECART));
			}
		}
	}

	@Override
	public Location getLocation() {
		Player player = Bukkit.getPlayer(playerId);
		if (player != null) {
			return player.getLocation();
		}
		return null;
	}

}
