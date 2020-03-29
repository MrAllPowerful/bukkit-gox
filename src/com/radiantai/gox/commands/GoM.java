package com.radiantai.gox.commands;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXUtils;
import com.radiantai.gox.structures.GoXPlayer;

public class GoM implements CommandExecutor {
	
	private GoX plugin;
	private Logger logger;
	private ConfigurationSection chatConfig;
	private ConfigurationSection config;
	
	public GoM(GoX plugin) {
		this.plugin = plugin;
		this.logger = Logger.getLogger("Minecraft");
		this.config = plugin.getConfig().getConfigurationSection("config");
		this.chatConfig = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
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
				sender.sendMessage(ChatColor.RED + GoXChat.chat("usage") + "/gom <command>");
			}
		}
		catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + GoXChat.chat("fatal error"));
			logger.warning(e.getMessage());
		}
		return false;
	}
	
	private void executeArgs(Player player, Command cmd, String cmdLabel, String[] args) {
		if (player.hasPermission("GoX.GoM")) {
			String action = args[0];
			switch (action) {
			case "add":
				executeAdd(player, args);
				break;
			case "addstation":
				executeAddstation(player, args);
				break;
			case "remove":
				executeRemove(player,args);
				break;
			case "removestation":
				executeRemovestation(player,args);
				break;
			case "link":
				executeLink(player, args);
				break;
			case "unlink":
				executeUnlink(player, args);
				break;
			case "info":
				executeInfo(player, args);
				break;
			case "findpath":
				executeFindpath(player, args);
				break;
			default:
				player.sendMessage(ChatColor.RED + GoXChat.chat("no such command") + ChatColor.WHITE + "/gom");
			}
		}
		else {
			player.sendMessage(ChatColor.DARK_RED + GoXChat.chat("no access"));
		}
	}
	
	private void executeAdd(Player player, String[] args) {
		if (player.hasMetadata("go_add")) {
			
			GoXPlayer gp = new GoXPlayer(player, plugin);
			Location location = gp.getAddNode();
			gp.setAddNode(null);
			
			player.removeMetadata("go_add", plugin);
			if (location.getBlock().getType() != Material.BRICK) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("place block"));
				return;
			}
			player.sendMessage(ChatColor.YELLOW + GoXChat.chat("adding node"));
			try {
				GoXMap.AddNode(location);
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("adding success"));
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("place block"));
			return;
		}
	}
	
	private void executeAddstation(Player player, String[] args) {
		if (player.hasMetadata("go_add_station")) {
			if (args.length < 2) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom addstation <name>");
				return;
			}
			String name = args[1];
			
			GoXPlayer gp = new GoXPlayer(player, plugin);
			Location location = gp.getAddstation();
			gp.setAddstation(null);
			
			player.removeMetadata("go_add_station", plugin);
			if (location.getBlock().getType() != Material.NETHERRACK) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("place station"));
				return;
			}
			player.sendMessage(ChatColor.YELLOW + "Adding a new station...");
			try {
				GoXMap.AddStation(name, location);
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("adding success"));
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("place station"));
			return;
		}
	}
	
	private void executeRemove(Player player, String[] args) {
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		if (node != null) {
			player.sendMessage(ChatColor.YELLOW+GoXChat.chat("removing"));
			try {
				GoXMap.RemoveNode(node.getId());
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN+GoXChat.chat("removing success"));
		}
		else {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
		}
	}
	
	private void executeRemovestation(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom removestation <name>");
			return;
		}
		String name = args[1];
		player.sendMessage(ChatColor.YELLOW+GoXChat.chat("removing"));
		try {
			GoXMap.RemoveStation(name);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
			return;
		}
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("removing success"));
	}
	
	private void executeUnlink(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom unlink <direction>");
			return;
		}
		String dir = args[1];
		
		if (!GoXUtils.isValidDirection(dir)) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("invalid direction"));
			return;
		}
		
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		
		if (node == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			return;
		}
		
		GoXNode linked = node.getLink(dir);
		
		if (linked == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no link"));
			return;
		}
		
		node.setLink(dir, null);
		linked.unlink(node.getId());
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("unlink success"));
	}
	
	private void executeLink(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id> <direction>");
			return;
		}
		else if (args.length > 2) {
			executeManualLink(player, args);
			return;
		}
		
		String id = args[1];
		
		Location location = player.getLocation();
		GoXNode from = GoXMap.GetNode(location);
		GoXNode to = GoXMap.GetNode(id);
		if (to == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such node"));
			return;
		}
		if (from == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			return;
		}
		player.sendMessage(ChatColor.YELLOW+GoXChat.chat("linking"));
		try {
			GoXMap.LinkNodes(from, to);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
			return;
		}
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("linking success"));
	}
	
	private void executeManualLink(Player player, String[] args) {
		if (args.length < 5) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id> <direction>");
			return;
		}
		String fromId = args[1].toLowerCase();
		String fromDir = args[2].toLowerCase();
		String toId = args[3].toLowerCase();
		String toDir = args[4].toLowerCase();
		
		if (!GoXUtils.isValidDirection(fromDir) || !GoXUtils.isValidDirection(toDir)) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("invalid direction"));
			return;
		}
		
		GoXNode from = GoXMap.GetNode(fromId);
		GoXNode to = GoXMap.GetNode(toId);
		
		if (from == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such node")+": "+fromId);
			return;
		}
		if (to == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such node")+": "+toId);
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW+GoXChat.chat("linking"));
		try {
			GoXMap.LinkNodesManual(from, fromDir, to, toDir);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
			return;
		}
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("linking success"));
	}
	
	private void executeInfo(Player player, String[] args) {
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		if (node != null) {
			GoXChat.fancyNodeExtended(player, node);
		}
		else {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
		}
	}
	
	private void executeFindpath(Player player, String[] args) {
		try {
			GoXPath path = GoXMap.FindPath(args[1], args[2]);
			player.sendMessage(ChatColor.GREEN + path.toString());
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
			return;
		}
	}
}
