package com.radiantai.gox.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

import com.radiantai.gox.GoX;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXDispenser;
import com.radiantai.gox.structures.GoXCartDispenserOwner;

public class GoXDispenseCart implements Listener {

	private GoX plugin;
	
	public GoXDispenseCart(GoX plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onCartDispense(BlockDispenseEvent e) {
		ItemStack item = e.getItem();
		if (item != null && item.isSimilar(new ItemStack(Material.MINECART))) {
			Block block = e.getBlock();
			if (block.getType() == Material.DISPENSER) {
				GoXDispenser gd = new GoXDispenser((Dispenser) block.getState());
				if (gd.isFacingRails()) {
					e.setCancelled(true);
					Minecart cart = gd.placeCart();
					if (cart != null) {
						GoXCart gc = new GoXCart(cart, plugin);
						gc.setOwner(new GoXCartDispenserOwner(block.getLocation()));
						gc.setTicksWhenleft(0);
						gd.scheduleRemoveCartFromInventory(plugin);
					}
				}
			}
		}
	}
}
