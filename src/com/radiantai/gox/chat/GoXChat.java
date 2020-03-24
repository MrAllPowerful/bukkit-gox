package com.radiantai.gox.chat;

import java.nio.charset.Charset;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXStation;

public class GoXChat {
	private static boolean cyrillicDecoder = false;
	private static ConfigurationSection chatConfig;
	
	public static void setupChat(GoX plugin) {
		cyrillicDecoder = plugin.getConfig().getConfigurationSection("lang").getBoolean("cyrillic decoding");
		chatConfig = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("commands");
	}
	
	public static String chat(String id) {
		String result = chatConfig.getString(id);
		
		if (cyrillicDecoder) {
			result = new String(result.getBytes(Charset.forName("Cp1251")),Charset.forName("UTF-8"));
		}
		
		return result;
	}
	
	public static void fancyStationCompact(Player p, GoXNode station) {
		if (station == null || !(station instanceof GoXStation)) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.GREEN+" "+((GoXStation) station).GetName());
	}
	
	public static void fancyStation(Player p, GoXStation station) {
		if (station == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.DARK_GREEN+chat("station")+":");
		p.sendMessage("  "+ChatColor.YELLOW+chat("name")+":"+ChatColor.GREEN+" "+station.GetName());
		p.sendMessage("  "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+station.getId());
		p.sendMessage("  "+ChatColor.GRAY+chat("location"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+station.getX());
		p.sendMessage("    "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+station.getY());
		p.sendMessage("   "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+station.getZ());;
	}
	
	public static void fancyStationExtended(Player p, GoXStation station) {
		if (station == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.DARK_GREEN+chat("station")+":");
		p.sendMessage("  "+ChatColor.YELLOW+chat("name")+":"+ChatColor.GREEN+" "+station.GetName());
		p.sendMessage("  "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+station.getId());
		p.sendMessage("  "+ChatColor.GRAY+chat("location"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+station.getX());
		p.sendMessage("    "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+station.getY());
		p.sendMessage("    "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+station.getZ());
		p.sendMessage("  "+ChatColor.GRAY+chat("connection"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("north")+":"+ChatColor.GREEN+" "+(station.getNorth() != null ? station.getNorth().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("east")+":"+ChatColor.GREEN+" "+(station.getEast() != null ? station.getEast().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("south")+":"+ChatColor.GREEN+" "+(station.getSouth() != null ? station.getSouth().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("west")+":"+ChatColor.GREEN+" "+(station.getWest() != null ? station.getWest().getId() : "-"));
	}
	
	public static void fancyNodeExtended(Player p, GoXNode node) {
		if (node == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		if (node instanceof GoXStation) {
			fancyStationExtended(p, (GoXStation) node);
			return;
		}
		p.sendMessage(ChatColor.DARK_BLUE+chat("node")+":");
		p.sendMessage("  "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+node.getId());
		p.sendMessage("  "+ChatColor.GRAY+chat("location"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+node.getX());
		p.sendMessage("    "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+node.getY());
		p.sendMessage("    "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+node.getZ());
		p.sendMessage("  "+ChatColor.GRAY+chat("connection"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("north")+":"+ChatColor.GREEN+" "+(node.getNorth() != null ? node.getNorth().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("east")+":"+ChatColor.GREEN+" "+(node.getEast() != null ? node.getEast().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("south")+":"+ChatColor.GREEN+" "+(node.getSouth() != null ? node.getSouth().getId() : "-"));
		p.sendMessage("    "+ChatColor.YELLOW+chat("west")+":"+ChatColor.GREEN+" "+(node.getWest() != null ? node.getWest().getId() : "-"));
	}
	
	public static void stationList(Player p, int page, int max) {
		Map<String,GoXStation> stations = GoXMap.GetStations();
		if (stations == null) {
			return;
		}
		int pages = stations.size()/max+1;
		p.sendMessage(ChatColor.DARK_GREEN+chat("list")+" ("+page+" / "+pages+"):");
		if (stations == null || stations.isEmpty()) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		int i = 0;
		int starting = (page-1)*max;
		int last = starting+max-1;
		for (GoXStation st : stations.values()) {
			if (starting<=i) {
				fancyStationCompact(p, st);
			}
			if (last==i) break;
			i++;
		}
	}
}
