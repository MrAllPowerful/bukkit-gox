package com.radiantai.gox.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoMap;
import com.radiantai.gox.pathfinding.GoNode;
import com.radiantai.gox.pathfinding.GoPath;
import com.radiantai.gox.pathfinding.Utils;

public class GoM implements CommandExecutor {
	
	private GoX plugin;
	private Logger logger;
	
	public GoM(GoX plugin) {
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
		case "setnext":
			executeSetnext(player, args);
			break;
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
		case "info":
			executeInfo(player, args);
			break;
		case "nodelist":
			executeNodelist(player, args);
			break;
		case "stationlist":
			executeStationlist(player, args);
			break;
		case "findpath":
			executeFindpath(player, args);
			break;
		}
	}

	private void executeSetnext(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: /gom setnext <direction>");
			return;
		}
		if (!Utils.isValidDirection(args[1])) {
			player.sendMessage(ChatColor.RED + "Invalid direction: "+args[1]);
			return;
		}
		player.setMetadata("go_next", new FixedMetadataValue(plugin, args[1]));
	}
	
	private void executeAdd(Player player, String[] args) {
		if (player.hasMetadata("go_add")) {
			Location location = (Location) player.getMetadata("go_add").get(0).value();
			player.removeMetadata("go_add", plugin);
			if (location.getBlock().getType() != Material.BRICK) {
				player.sendMessage(ChatColor.RED + "Place a node-block and a rail first!");
				return;
			}
			player.sendMessage(ChatColor.YELLOW + "Adding a new node...");
			try {
				GoMap.AddNode((int) location.getX(), (int) location.getY(), (int) location.getZ());
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Successfully added!");
		}
		else {
			player.sendMessage(ChatColor.RED + "Place a node-block and a rail first!");
			return;
		}
	}
	
	private void executeAddstation(Player player, String[] args) {
		if (player.hasMetadata("go_add_station")) {
			if (args.length < 2) {
				player.sendMessage(ChatColor.RED + "Usage: /gom addstation <name>");
				return;
			}
			String name = args[1];
			Location location = (Location) player.getMetadata("go_add_station").get(0).value();
			player.removeMetadata("go_add_station", plugin);
			if (location.getBlock().getType() != Material.NETHERRACK) {
				player.sendMessage(ChatColor.RED + "Place a station-block and a rail first!");
				return;
			}
			player.sendMessage(ChatColor.YELLOW + "Adding a new station...");
			try {
				GoMap.AddStation(name, (int) location.getX(), (int) location.getY(), (int) location.getZ());
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Successfully added!");
		}
		else {
			player.sendMessage(ChatColor.RED + "Place a node-block and a rail first!");
			return;
		}
	}
	
	private void executeRemove(Player player, String[] args) {
		Location location = player.getLocation();
		GoNode node = GoMap.GetNode((int) location.getX(), (int) location.getZ());
		if (node != null) {
			player.sendMessage(ChatColor.YELLOW+"Removing...");
			try {
				GoMap.RemoveNode(node.getId());
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
				return;
			}
			player.sendMessage(ChatColor.GREEN+"Successfully removed!");
		}
		else {
			player.sendMessage(ChatColor.RED+"You must stand over a registered node!");
		}
	}
	
	private void executeRemovestation(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: /gom removestation <name>");
			return;
		}
		String name = args[1];
		player.sendMessage(ChatColor.YELLOW+"Removing...");
		try {
			GoMap.RemoveStation(name);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
			return;
		}
		player.sendMessage(ChatColor.GREEN+"Successfully removed!");
	}
	
	private void executeLink(Player player, String[] args) {
		if (args.length < 3) {
			player.sendMessage(ChatColor.RED + "Usage: /gom link <x> <z>");
			return;
		}
		int x,z;
		try {
			x = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Usage: /gom link <number> <number>");
			return;
		}
		
		Location location = player.getLocation();
		GoNode node = GoMap.GetNode((int) location.getX(), (int) location.getZ());
		GoNode to = GoMap.GetNode(x, z);
		if (to == null) {
			player.sendMessage(ChatColor.RED+"There are no nodes/stations at specified coordinates!");
			return;
		}
		if (node == null) {
			player.sendMessage(ChatColor.RED+"You must stand over a registered node/station!");
			return;
		}
		player.sendMessage(ChatColor.YELLOW+"Linking...");
		try {
			GoMap.LinkNodes(node, to);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
			return;
		}
		player.sendMessage(ChatColor.GREEN+"Successfully linked!");
	}
	
	private void executeInfo(Player player, String[] args) {
		Location location = player.getLocation();
		GoNode node = GoMap.GetNode((int) location.getX(), (int) location.getZ());
		if (node != null) {
			player.sendMessage(node.toString());
		}
		else {
			player.sendMessage(ChatColor.RED+"You must stand over a registered node/station!");
		}
	}
	
	private void executeNodelist(Player player, String[] args) {
		GoMap.MessageNodes(player);
	}
	
	private void executeStationlist(Player player, String[] args) {
		GoMap.MessageStations(player);
	}
	
	private void executeFindpath(Player player, String[] args) {
		try {
			GoPath path = GoMap.FindPath(args[1], args[2]);
			player.sendMessage(ChatColor.GREEN + path.toString());
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Fail! Reason: " + ChatColor.RED + e.getMessage());
			return;
		}
	}
}
