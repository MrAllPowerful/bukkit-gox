package com.radiantai.gox.structures;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;

public class GoXChest {
	
	private Block chest;
	private GoX plugin;
	
	public GoXChest(Block chest, GoX plugin) {
		this.chest = chest;
		this.plugin = plugin;
	}
	
	public Minecart placeCart() {
		Minecart cart = null;
		DirectionalContainer data = (DirectionalContainer) chest.getState().getData();
		Block onto = chest.getRelative(data.getFacing());
		cart = GoXCart.createCart(onto.getLocation().clone().add(new Vector(0.5,0,0.5)));
		return cart;
	}
	
	public boolean isFacingRails() {
		DirectionalContainer data = (DirectionalContainer) chest.getState().getData();
		Block onto = chest.getRelative(data.getFacing());
		return GoXRail.isRails(onto);
	}
	
	public boolean hasCartInside() {
		Inventory inventory = ((Chest) chest.getState()).getInventory();
		int cartPos = inventory.first(new ItemStack(Material.MINECART));
		return cartPos >= 0;
	}
	
	public boolean isSingle() {
		InventoryHolder holder = ((Chest) chest.getState()).getInventory().getHolder();
		return !(holder instanceof DoubleChest);
	}
	
	public void removeCartFromInventory() {
		Inventory inventory = ((Chest) chest.getState()).getInventory();
		int cartPos = inventory.first(new ItemStack(Material.MINECART));
		if (cartPos >= 0) {
			inventory.setItem(cartPos, null);
		}
	}	
	
	public void lockCartSpawn() {
		chest.setMetadata("go_cartspawnlock", new FixedMetadataValue(plugin, true));
	}
	
	public void unlockCartSpawn() {
		chest.setMetadata("go_cartspawnlock", new FixedMetadataValue(plugin, false));
	}
	
	public boolean getCartSpawnLock() {
		boolean result = false;
		if (chest.hasMetadata("go_cartspawnlock")) {
			return chest.getMetadata("go_cartspawnlock").get(0).asBoolean();
		}
		return result;
	}
}
