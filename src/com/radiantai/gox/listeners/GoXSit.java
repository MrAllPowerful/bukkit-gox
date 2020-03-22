package com.radiantai.gox.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoMap;
import com.radiantai.gox.pathfinding.GoNode;
import com.radiantai.gox.pathfinding.GoPath;
import com.radiantai.gox.pathfinding.Utils;

public class GoXSit implements Listener {
	private GoX plugin;
	private Logger logger;
	private ConfigurationSection config;
	
	public GoXSit(GoX plugin, Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
		this.config = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
	}
	
	@EventHandler
	public void onCartSit(VehicleEnterEvent e){
		if (!(e.getVehicle() instanceof Minecart)) {
			return;
		}
		if (!(e.getEntered() instanceof Player)) {
			return;
		}
		Block block = e.getVehicle().getLocation().getBlock();
		if (block.getType() != Material.RAILS) {
			return;
		}
		if (block.getRelative(BlockFace.DOWN).getType() != Material.NETHERRACK &&
				block.getRelative(BlockFace.DOWN).getType() != Material.BRICK) {
			return;
		}
		GoNode node = GoMap.GetNode((int) block.getX(), (int) block.getZ());
		if (node == null) {
			return;
		}
		Minecart cart = (Minecart) e.getVehicle();
		Player player = (Player) e.getEntered();
		if (player.hasMetadata("go_destination")) {
			String finish = player.getMetadata("go_destination").get(0).asString();
			if (finish != null) {
				player.sendMessage(ChatColor.YELLOW+config.getString("searching path"));
				GoPath path = GoMap.FindPath(node.getId(), finish);
				if (path == null || path.IsEmpty()) {
					player.sendMessage(ChatColor.RED+config.getString("path not found"));
					Utils.resetPathMeta(player, plugin);
					return;
				}
				Utils.resetPathMeta(player, plugin);
				String startDirection = path.Peek();
				player.setMetadata("go_next", new FixedMetadataValue(plugin, path.Pop()));
				player.setMetadata("go_path", new FixedMetadataValue(plugin, path));
				player.sendMessage(ChatColor.GREEN+config.getString("path found"));
				cart.setMaxSpeed(cart.getMaxSpeed()*1.3);
				Vector v = Utils.getVector(startDirection).multiply(cart.getMaxSpeed()*0.5);
				cart.setVelocity(v);
			}
			else {
				player.sendMessage(ChatColor.RED+config.getString("no destination"));
			}
		}
		else {
			player.sendMessage(ChatColor.RED+config.getString("no destination"));
		}
	}
}
