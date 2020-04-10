package com.radiantai.gox.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXStation;
import com.radiantai.gox.structures.GoXException;
import com.radiantai.gox.structures.GoXPermissionException;
import com.radiantai.gox.structures.GoXPlayer;

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
				throw new Exception("You must be a player to execute this command!");
			}
			
			Player player = (Player) sender;
			
			if (args.length > 0) {
				executeArgs(player, cmd, cmdLabel, args);
			}
			else {
				sender.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/go <command> "+GoXChat.chat("or")+" /go <station>");
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
		if (player.hasPermission("GoX.Go")) {
			String action = args[0];
			switch (action) {
			case "cancel":
				executeCancel(player, args);
				break;
			case "current":
				executeCurrent(player, args);
				break;
			case "info":
				executeInfo(player, args);
				break;
			case "list":
				executeList(player, args);
				break;
			case "closest":
				executeClosest(player, args);
				break;
			default:
				executeGo(player, args);
				break;
			}
		}
		else {
			player.sendMessage(ChatColor.DARK_RED + GoXChat.chat("no access"));
		}
	}
	
	private void executeClosest(Player player, String[] args) throws Exception {
		GoXChat.closestList(player, 3);
	}
	
	private void executeList(Player player, String[] args) throws Exception {
		int page = 1;
		if (args.length > 1) {
			try {
				page = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/go list <page number>");
				return;
			}
		}
		if (page <= 0) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/go list <page number>");
			return;
		}
		GoXChat.stationList(player, page, 5);
		
	}

	private void executeInfo(Player player, String[] args) throws Exception {
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("usage")+"/go info <station name>");
			return;
		}
		String name = args[1];
		GoXStation st = GoXMap.GetStation(name);
		if (st != null) {
			GoXChat.fancyStation(player, st);
		}
		else {
			player.sendMessage(ChatColor.RED+GoXChat.chat("no such station"));
		}
		
	}

	private void executeGo(Player player, String[] args) throws Exception {
		GoXStation st = GoXMap.GetStation(args[0]);
		if (st == null) {
			player.sendMessage(ChatColor.RED + GoXChat.chat("no station"));
			return;
		}
		String id = st.getId();
		GoXPlayer p = new GoXPlayer(player, plugin);
		p.resetPath();
		p.setDestination(id);
		player.sendMessage(ChatColor.GREEN + GoXChat.chat("sit"));
	}
	
	private void executeCancel(Player player, String[] args) throws Exception {
		GoXPlayer p = new GoXPlayer(player, plugin);
		p.reset();
		player.sendMessage(ChatColor.GREEN + GoXChat.chat("canceled"));
	}
	
	private void executeCurrent(Player player, String[] args) throws Exception {
		GoXPlayer p = new GoXPlayer(player, plugin);
		String id = p.getDestination();
		if (id != null) {
			GoXNode destination = GoXMap.GetNode(id);
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("destination"));
			if (destination instanceof GoXStation) {
				GoXChat.fancyStationCompact(player,(GoXStation) destination);
			}
		}
		else {
			player.sendMessage(ChatColor.RED + GoXChat.chat("no destination"));
		}
	}

}
