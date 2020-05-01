package com.radiantai.gox.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXCartPlayerOwner;
import com.radiantai.gox.structures.GoXRail;

public class GoXPlaceCart implements Listener {
	private GoX plugin;
	
	public GoXPlaceCart(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCartPlace(PlayerInteractEvent e) {
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		Block block = e.getClickedBlock();
		ItemStack item = e.getItem();
		
		if (block == null || !GoXRail.isRails(block)) {
			return;
		}
		
		if (item == null || !item.isSimilar(new ItemStack(Material.MINECART))) {
			return;
		}
		
		e.setCancelled(true);
		
		int slotNumber = e.getPlayer().getInventory().getHeldItemSlot();
		e.getPlayer().getInventory().setItem(slotNumber, null);
		Minecart cart = GoXCart.createCart(block.getLocation().add(new Vector(0.5,0,0.5)));
		if (cart != null) {
			GoXCart gc = new GoXCart(cart, plugin);
			gc.setOwner(new GoXCartPlayerOwner(e.getPlayer().getUniqueId()));
			gc.setTicksWhenleft(0);
		}
	}
}
