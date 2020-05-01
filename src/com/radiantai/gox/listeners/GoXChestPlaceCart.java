package com.radiantai.gox.listeners;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXCart;
import com.radiantai.gox.structures.GoXCartContainerOwner;
import com.radiantai.gox.structures.GoXChest;

public class GoXChestPlaceCart implements Listener {
private GoX plugin;
	
	public GoXChestPlaceCart(GoX plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onCartDispense(BlockRedstoneEvent e) {
		Block block = e.getBlock();
		if (e.getOldCurrent() == 0 && e.getNewCurrent() > 0) {
			LinkedList<Block> affected = new LinkedList<Block>();
			affected.addAll(GoXUtils.getFacingBlocks(block));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.NORTH)));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.EAST)));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.SOUTH)));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.WEST)));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.UP)));
			affected.addAll(GoXUtils.getFacingBlocks(block.getRelative(BlockFace.DOWN)));
			
			for (Block b : affected) {
				if (b.getType() == Material.CHEST && !b.isBlockIndirectlyPowered() && !b.isBlockPowered()) {
					GoXChest gchest = new GoXChest(b, plugin);
					if (gchest.isSingle() && gchest.hasCartInside() && !gchest.getCartSpawnLock()) {
						gchest.lockCartSpawn();
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
							@Override
							public void run() {
								gchest.unlockCartSpawn();
								gchest.hasCartInside();
								if (b.isBlockIndirectlyPowered() || b.isBlockPowered()) {
									Minecart cart = gchest.placeCart();
									if (cart != null) {
										GoXCart gc = new GoXCart(cart, plugin);
										gc.setOwner(new GoXCartContainerOwner(b.getLocation()));
										gc.setTicksWhenleft(0);
										gchest.removeCartFromInventory();
									}
								}
							}
						},1);
					}
				}
			}
		}
	}
}
