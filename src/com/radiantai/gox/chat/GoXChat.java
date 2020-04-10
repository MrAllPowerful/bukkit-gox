package com.radiantai.gox.chat;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.pathfinding.GoXNode;
import com.radiantai.gox.pathfinding.GoXPath;
import com.radiantai.gox.pathfinding.GoXStation;

public class GoXChat {
	private static GoX goxplugin = null;
	private static boolean cyrillicDecoder = false;
	private static ConfigurationSection chatConfig;
	
	public static void setupChat(GoX plugin) {
		goxplugin = plugin;
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
	
	public static void fancyStationCompact(Player p, GoXStation station) {
		if (station == null || !(station instanceof GoXStation)) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.GREEN+"  "+((GoXStation) station).GetName());
	}
	
	public static void fancyStationWithDistance(Player p, GoXStation station) {
		if (station == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		int distance = (int) station.getLocation().distance(p.getLocation());
		p.sendMessage(" "+ChatColor.YELLOW+chat("name")+":"+ChatColor.GREEN+" "+station.GetName()
				+ChatColor.YELLOW+" "+chat("distance")+": "+ChatColor.GREEN+distance
				+ChatColor.YELLOW+" "+chat("x")+" : "+ChatColor.GREEN+station.getX()
				+ChatColor.YELLOW+" "+chat("y")+" : "+ChatColor.GREEN+station.getY()
				+ChatColor.YELLOW+" "+chat("z")+" : "+ChatColor.GREEN+station.getZ());
	}
	
	public static void fancyStation(Player p, GoXStation station) {
		if (station == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.DARK_GREEN+chat("station")+":");
		p.sendMessage(" "+ChatColor.YELLOW+chat("name")+":"+ChatColor.GREEN+" "+station.GetName());
		p.sendMessage(" "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+station.getId());
		p.sendMessage(" "+ChatColor.GRAY+chat("location"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+station.getX());
		p.sendMessage("   "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+station.getY());
		p.sendMessage("   "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+station.getZ());;
	}
	
	public static void fancyStationExtended(Player p, GoXStation station) {
		if (station == null) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		p.sendMessage(ChatColor.DARK_GREEN+chat("station")+":");
		p.sendMessage(" "+ChatColor.YELLOW+chat("name")+":"+ChatColor.GREEN+" "+station.GetName());
		p.sendMessage(" "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+station.getId());
		p.sendMessage(" "+ChatColor.GRAY+chat("location"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+station.getX());
		p.sendMessage("   "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+station.getY());
		p.sendMessage("   "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+station.getZ());
		p.sendMessage("   "+ChatColor.YELLOW+chat("world")+":"+ChatColor.GREEN+" "+station.getWorld());
		p.sendMessage(" "+ChatColor.GRAY+chat("connection"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("north")+":"+" "+ChatColor.GREEN+(station.getNorth() != null ? station.getNorth().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("east")+":"+" "+ChatColor.GREEN+(station.getEast() != null ? station.getEast().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("south")+":"+" "+ChatColor.GREEN+(station.getSouth() != null ? station.getSouth().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("west")+":"+" "+ChatColor.GREEN+(station.getWest() != null ? station.getWest().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("force")+":"+ChatColor.GREEN+" "+(station.getForceDirection() != null ? station.getForceDirection() : "-"));
		String references = referencesToString(station.getReferences());
		p.sendMessage("   "+ChatColor.YELLOW+chat("references")+":"+" "+references);
		p.sendMessage(" "+ChatColor.GRAY+chat("drop point")+":");
		if (station.getDropPoint() != null) {
			p.sendMessage("   "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+station.getDropPoint().getX());
			p.sendMessage("   "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+station.getDropPoint().getY());
			p.sendMessage("   "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+station.getDropPoint().getZ());
		}
		else {
			p.sendMessage("   "+ChatColor.YELLOW+"<"+chat("empty")+">");
		}
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
		p.sendMessage(ChatColor.BLUE+chat("node")+":");
		p.sendMessage(" "+ChatColor.YELLOW+chat("id")+":"+ChatColor.GREEN+" "+node.getId());
		p.sendMessage(" "+ChatColor.GRAY+chat("location"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("x")+":"+ChatColor.GREEN+" "+node.getX());
		p.sendMessage("   "+ChatColor.YELLOW+chat("y")+":"+ChatColor.GREEN+" "+node.getY());
		p.sendMessage("   "+ChatColor.YELLOW+chat("z")+":"+ChatColor.GREEN+" "+node.getZ());
		p.sendMessage("   "+ChatColor.YELLOW+chat("world")+":"+ChatColor.GREEN+" "+node.getWorld());
		p.sendMessage(" "+ChatColor.GRAY+chat("connection"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("north")+":"+" "+ChatColor.GREEN+(node.getNorth() != null ? node.getNorth().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("east")+":"+" "+ChatColor.GREEN+(node.getEast() != null ? node.getEast().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("south")+":"+" "+ChatColor.GREEN+(node.getSouth() != null ? node.getSouth().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("west")+":"+" "+ChatColor.GREEN+(node.getWest() != null ? node.getWest().chatView() : "-"));
		p.sendMessage("   "+ChatColor.YELLOW+chat("force")+":"+ChatColor.GREEN+" "+(node.getForceDirection() != null ? node.getForceDirection() : "-"));
		String references = referencesToString(node.getReferences());
		p.sendMessage("   "+ChatColor.YELLOW+chat("references")+":"+" "+references);
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
		for (String key : stations.keySet()) {
			if (starting<=i) {
				fancyStationCompact(p, stations.get(key));
			}
			if (last==i) break;
			i++;
		}
	}
	
	public static void closestList(Player p, int n) {
		List<GoXStation> list = GoXMap.getClosestStations(p.getLocation());
		p.sendMessage(ChatColor.DARK_GREEN+chat("closest")+":");
		if (list==null || list.isEmpty()) {
			p.sendMessage(ChatColor.YELLOW+"<"+chat("empty")+">");
			return;
		}
		for (GoXStation station : list) {
			fancyStationWithDistance(p, station);
			n--;
			if (n<=0) break;
		}
	}
	
	public static void estimatedTime(Player p, GoXPath path) {
		double estimated = path.getDistance()/7.5/goxplugin.getCartMaxSpeed();
		int minutes = (int) Math.floor(estimated/60.0);
		int seconds = (int) Math.ceil(estimated-minutes*60.0);
		String message = ChatColor.GREEN + chat("estimated time")+": "+ChatColor.WHITE;
		message += minutes==0 ? "" : minutes+" "+chat("minutes")+" ";
		message += seconds==0 ? "" : seconds+" "+chat("seconds");
		message += ".";
		p.sendMessage(message);
	}
	
	private static String referencesToString(List<GoXNode> references) {
		String result = "";
		for (GoXNode ref : references) {
			result += ref.chatView() + " ";
		}
		return result;
	}
}
