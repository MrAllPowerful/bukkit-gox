package com.radiantai.gox.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoMap;
import com.radiantai.gox.pathfinding.GoStation;
import com.radiantai.gox.pathfinding.Utils;

public class Go implements CommandExecutor {
	
	private GoX plugin;
	private Logger logger;
	
	public Go(GoX plugin) {
		this.plugin = plugin;
		this.logger = Logger.getLogger("Minecraft");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be a player to execute this command!");
				return false;
			}
			
			Player player = (Player) sender;
			
			if (args.length > 0) {
				executeArgs(player, cmd, cmdLabel, args);
			}
			else {
				sender.sendMessage(ChatColor.RED + "Usage: /gom <command>");
			}
		}
		catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Error occured executing command!");
			logger.warning(e.getMessage());
		}
		return false;
	}
	
	private void executeArgs(Player player, Command cmd, String cmdLabel, String[] args) {
		String action = args[0];
		switch (action) {
		case "cancel":
			executeCancel(player, args);
			break;
		case "current":
			executeCurrent(player, args);
			break;
		default:
			executeGo(player, args);
			break;
		}
	}
	
	private void executeGo(Player player, String[] args) {
		GoStation st = GoMap.GetStation(args[0]);
		if (st == null) {
			player.sendMessage(ChatColor.RED + "There is no such station or command.");
			return;
		}
		String id = st.getId();
		Utils.resetPathMeta(player, plugin);
		player.setMetadata("go_destination", new FixedMetadataValue(plugin, id));
		player.sendMessage(ChatColor.GREEN + "Sit in a cart on a station block, please.");
	}
	
	private void executeCancel(Player player, String[] args) {
		Utils.resetPathMeta(player, plugin);
		player.sendMessage(ChatColor.GREEN + "Canceled.");
	}
	
	private void executeCurrent(Player player, String[] args) {
		if (player.hasMetadata("go_destination")) {
			player.sendMessage(ChatColor.GREEN + "Current destination: "+player.getMetadata("go_destination").get(0).asString());
		}
	}

}
