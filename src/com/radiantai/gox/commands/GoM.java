package com.radiantai.gox.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXDirection;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXStation;
import com.radiantai.gox.structures.GoXException;
import com.radiantai.gox.structures.GoXPermissionException;
import com.radiantai.gox.structures.GoXPlayer;

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
				throw new Exception("You must be a player to execute this command!");
			}
			
			Player player = (Player) sender;
			
			if (args.length > 0) {
				executeArgs(player, cmd, cmdLabel, args);
			}
			else {
				sender.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom <command>");
			}
		}
		catch (GoXPermissionException e) {
			sender.sendMessage(ChatColor.DARK_RED + GoXChat.chat("no access"));
		}
		catch (GoXException e) {
			sender.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
		}
		catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + GoXChat.chat("fatal error"));
			logger.warning(e.getMessage());
		}
		return false;
	}
	
	private void executeArgs(Player player, Command cmd, String cmdLabel, String[] args) throws Exception {
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
			case "force":
				executeForce(player, args);
				break;
			case "unforce":
				executeUnforce(player, args);
				break;
			case "setdrop":
				executeSetdrop(player, args);
				break;
			case "rename":
				executeRename(player, args);
				break;
			default:
				player.sendMessage(ChatColor.RED + GoXChat.chat("no such command") + ChatColor.WHITE + "/gom");
			}
		}
		else {
			player.sendMessage(ChatColor.DARK_RED + GoXChat.chat("no access"));
		}
	}
	
	private void executeRename(Player player, String[] args) throws Exception {
		if (args.length < 3) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom rename <old-name> <new-name>");
			return;
		}
		String oldName = args[1];
		String newName = args[2];
		
		GoXMap.renameStation(oldName, newName);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("rename success"));
	}

	private void executeSetdrop(Player player, String[] args) throws Exception {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom setdrop <name>");
			return;
		}
		GoXStation st = GoXMap.GetStation(args[1]);
		
		if (st == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such station"));
			return;
		}
		
		Location location = player.getLocation();
		
		st.setDropPoint(location);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("drop point success"));
		
	}

	private void executeUnforce(Player player, String[] args) throws Exception {
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		
		if (node == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			return;
		}
		
		node.setForceDirection(null);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("unforce success"));
	}

	private void executeForce(Player player, String[] args) throws Exception {
		
		String dir = new GoXPlayer(player, plugin).getPlayerDirection();
		GoXDirection gd = new GoXDirection(dir);
		
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		
		if (node == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			return;
		}
		
		node.setForceDirection(gd);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("force success"));
		
	}

	private void executeAdd(Player player, String[] args) throws Exception {
		
		GoXPlayer gp = new GoXPlayer(player, plugin);
		Location location = gp.getAddNode();
		
		if (location != null) {
			gp.resetAdd();
			
			player.removeMetadata("go_add", plugin);
			if (location.getBlock().getType() != plugin.getNodeBlock()) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("place block"));
				return;
			}
			player.sendMessage(ChatColor.YELLOW + GoXChat.chat("adding node"));
			
			GoXMap.AddNode(location);
			
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("adding success"));
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("place block"));
			return;
		}
	}
	
	private void executeAddstation(Player player, String[] args) throws Exception {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom addstation <name>");
			return;
		}
		String name = args[1];
		
		GoXPlayer gp = new GoXPlayer(player, plugin);
		Location location = gp.getAddstation();
		
		if (location != null) {
			gp.resetAdd();
			
			player.removeMetadata("go_add_station", plugin);
			if (location.getBlock().getType() != plugin.getStationBlock()) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("place station"));
				return;
			}
			player.sendMessage(ChatColor.YELLOW + "Adding a new station...");
			
			GoXMap.AddStation(name, location);
			
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("adding success"));
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("place station"));
			return;
		}
	}
	
	private void executeRemove(Player player, String[] args) throws Exception {
		Location location = player.getLocation();
		GoXNode node = GoXMap.GetNode(location);
		if (node != null) {
			player.sendMessage(ChatColor.YELLOW+GoXChat.chat("removing"));
			
			GoXMap.RemoveNode(node.getId());
			
			player.sendMessage(ChatColor.GREEN+GoXChat.chat("removing success"));
		}
		else {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
		}
	}
	
	private void executeRemovestation(Player player, String[] args) throws Exception {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom removestation <name>");
			return;
		}
		String name = args[1];
		player.sendMessage(ChatColor.YELLOW+GoXChat.chat("removing"));
		
		GoXMap.RemoveStation(name);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("removing success"));
	}
	
	private void executeUnlink(Player player, String[] args) throws Exception {
		
		GoXNode node = null;
		GoXDirection gd = null;
		
		if (args.length == 2) {
			Location location = player.getLocation();
			node = GoXMap.GetNode(location);
			gd = new GoXDirection(args[1]);
			if (node == null) {
				player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
				return;
			}
		}
		else if (args.length == 3) {
			node = GoXMap.GetNode(args[1]);
			gd = new GoXDirection(args[2]);
			if (node == null) {
				player.sendMessage(ChatColor.RED+GoXChat.chat("no such node"));
				return;
			}
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom unlink [id] <direction>");
			return;
		}
		
		GoXNode linked = node.getLink(gd);
		
		if (linked == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no link"));
			return;
		}
		
		node.unlink(gd);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("unlink success"));
	}
	
	private void executeLink(Player player, String[] args) throws Exception {
		if (args.length == 2) {
			executeAutoLink(player, args);
			return;
		}
		else if (args.length == 3) {
			executeOnesidedLink(player, args);
			return;
		}
		else if (args.length == 4) {
			executeManualOnesidedLink(player, args);
			return;
		}
		else if (args.length == 5) {
			executeManualLink(player, args);
			return;
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <id>");
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <direction> <id>");
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id>");
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id> <direction>");
			return;
		}
	}
	
	private void executeAutoLink(Player player, String[] args) throws Exception {
		
		String id = args[1].toLowerCase();
		
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
		
		from.autoLink(to);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("linking success"));
	}
	
	private void executeManualLink(Player player, String[] args) throws Exception {
		if (args.length < 5) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id> <direction>");
			return;
		}
		String fromId = args[1].toLowerCase();
		GoXDirection fromDir = new GoXDirection(args[2]);
		String toId = args[3].toLowerCase();
		GoXDirection toDir = new GoXDirection(args[4]);
		
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
	
	private void executeManualOnesidedLink(Player player, String[] args) throws Exception {
		if (args.length < 4) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/gom link <from-id> <direction> <to-id>");
			return;
		}
		String fromId = args[1].toLowerCase();
		GoXDirection fromDir = new GoXDirection(args[2]);
		String toId = args[3].toLowerCase();
		
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
			GoXMap.LinkNodesManualOnesided(from, fromDir, to);
		}
		catch (Exception e) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("fail reason") + ChatColor.RED + e.getMessage());
			return;
		}
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("linking success"));
	}
	
	private void executeOnesidedLink(Player player, String[] args) throws Exception {
		
		String id = args[2].toLowerCase();
		GoXDirection dir = new GoXDirection(args[1]);
		
		Location location = player.getLocation();
		GoXNode from = GoXMap.GetNode(location);
		GoXNode to = GoXMap.GetNode(id);
		
		if (from == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			return;
		}
		
		if (to == null) {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such node"));
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW+GoXChat.chat("linking"));
		
		from.setLink(dir, to);
		
		player.sendMessage(ChatColor.GREEN+GoXChat.chat("linking success"));
	}
	
	private void executeInfo(Player player, String[] args) throws Exception {
		if (args.length > 1) {
			GoXNode node = GoXMap.GetNode(args[1]);
			if (node != null) {
				GoXChat.fancyNodeExtended(player, node);
			}
			else {
				player.sendMessage(ChatColor.RED+GoXChat.chat("no such node"));
			}
		}
		else {
			Location location = player.getLocation();
			GoXNode node = GoXMap.GetNode(location);
			if (node != null) {
				GoXChat.fancyNodeExtended(player, node);
			}
			else {
				player.sendMessage(ChatColor.RED+GoXChat.chat("stand over"));
			}
		}
	}
	
	private void executeFindpath(Player player, String[] args) throws Exception {
		GoXPath path = GoXMap.FindPath(args[1], args[2]);
		if (path == null) {
			throw new GoXException("null path");
		}
		player.sendMessage(ChatColor.GREEN + path.toString());
	}
}
