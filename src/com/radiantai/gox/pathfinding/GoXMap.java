package com.radiantai.gox.pathfinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;

public class GoXMap {
	private static List<GoXNode> nodes = new ArrayList<GoXNode>();
	private static Map<String, GoXStation> stations = new HashMap<String,GoXStation>();
	private static GoX plugin;
	private static ConfigurationSection config;
	private static Logger logger;
	
	public static void SetupPlugin(GoX setPlugin, Logger setLogger) {
		plugin = setPlugin;
		logger = setLogger;
		config = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("other");
	}
	
	public static void AddNode(GoXNode node) {
		nodes.add(node);
	}
	
	public static void AddNode(int x, int y, int z) throws Exception {
		GoXNode node = GoXMap.GetNode(x, z);
		if (node != null) {
			throw new Exception(GoXChat.chat("already location"));
		}
		nodes.add(new GoXNode(x, y, z));
	}
	
	public static void AddStation(GoXStation station) {
		if (nodes != null) {
			nodes.add(station);
		}
	}
	
	public static void AddStation(String name, int x, int y, int z) throws Exception {
		String idName = name.toLowerCase();
		GoXNode node = GoXMap.GetNode(x, z);
		if (node != null) {
			throw new Exception(GoXChat.chat("already location"));
		}
		if (stations.get(idName) != null) {
			throw new Exception(GoXChat.chat("already name"));
		}
		if (!GoXUtils.validateName(name)) {
			throw new Exception(GoXChat.chat("invalid name"));
		}
		List<String> reserved = config.getStringList("prohibited stations");
		if (reserved.contains(idName)) {
			throw new Exception(GoXChat.chat("name reserved"));
		}
		GoXStation newst = new GoXStation(name, x, y, z);
		nodes.add(newst);
		stations.put(idName, newst);
	}
	
	public static void RemoveStation(String name) throws Exception {
		if (stations != null) {
			GoXStation st = stations.get(name);
			if (st == null) {
				throw new Exception(GoXChat.chat("no such station"));
			}
			RemoveNodeP(st.getId());
			stations.remove(name);
		}
	}
	
	public static void RemoveNode(String id) throws Exception {
		GoXNode node = GetNode(id);
		if (node instanceof GoXStation) {
			throw new Exception(GoXChat.chat("cannot remove node"));
		}
		RemoveNodeP(id);
	}
	
	private static void RemoveNodeP(String id) throws Exception {
		if (nodes != null) {
			GoXNode node = GetNode(id);
			if (node.north != null) {
				node.north.SetSouth(null);
			}
			if (node.south != null) {
				node.south.SetNorth(null);
			}
			if (node.east != null) {
				node.east.SetWest(null);
			}
			if (node.west != null) {
				node.west.SetEast(null);
			}
			if (!nodes.removeIf(n -> (n.getId() == id))) {
				throw new Exception(GoXChat.chat("no such node"));
			}
		}
	}
	
	public static GoXNode GetNode(int x, int z) {
		List<GoXNode> list = nodes.stream()
		.filter(node -> (node.getX() == x && node.getZ() == z))
		.collect(Collectors.toList());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public static GoXNode GetNode(String id) {
		List<GoXNode> list = nodes.stream()
		.filter(node -> (node.getId().equals(id)))
		.collect(Collectors.toList());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public static GoXStation GetStation(String name) {
		GoXStation st = stations.get(name);
		return st;
	}
	
	
	public static void LinkNodes(GoXNode from, GoXNode to) throws Exception {
		if (from.getX()==to.getX() && from.getZ()==to.getZ()) {
			throw new Exception(GoXChat.chat("to itself"));
		}
		if (from.getZ()==to.getZ()) {
			if (from.getX() > to.getX()) {
				from.SetWest(to);
				to.SetEast(from);
			}
			else {
				from.SetEast(to);
				to.SetWest(from);
			}
		}
		else if (from.getX()==to.getX()) {
			if (from.getZ() > to.getZ()) {
				from.SetNorth(to);
				to.SetSouth(from);
			}
			else {
				from.SetSouth(to);
				to.SetNorth(from);
			}
		}
		else {
			throw new Exception(GoXChat.chat("one line"));
		}
	}
	
	public static void MessageNodes(Player player) {
		player.sendMessage(ChatColor.AQUA+GoXChat.chat("nodes"));
		if (!nodes.isEmpty()) {
			for (GoXNode node : nodes) {
				player.sendMessage(ChatColor.GREEN + " "+node.toString());
			}
		}
		else {
			player.sendMessage(ChatColor.YELLOW+"<"+GoXChat.chat("empty")+">");
		}
	}
	
	public static void MessageStations(Player player) {
		player.sendMessage(ChatColor.AQUA+GoXChat.chat("stations"));
		if (!nodes.isEmpty()) {
			for (GoXStation st : new ArrayList<GoXStation>(stations.values())) {
				player.sendMessage(ChatColor.GREEN + " "+st.toString());
			}
		}
		else {
			player.sendMessage(ChatColor.RED+"<"+GoXChat.chat("empty")+">");
		}
	}
	
	public static GoXPath FindPath(String start, String finish) {
		Queue<GoXNode> searchQueue = new LinkedList<GoXNode>();
		ArrayList<String> visited = new ArrayList<String>();
		GoXNode first = GetNode(start).clone();
		GoXPath path = new GoXPath();
		searchQueue.add(first);
		while (!searchQueue.isEmpty()) {
			GoXNode curr = searchQueue.poll();
			visited.add(curr.id);
			if (curr.getId().equals(finish)) {
				while (curr.prev != null) {
					path.Push(curr.getFromPrev());
					curr = curr.getPrev();
				}
				return path;
			}
			if (curr.getNorth() != null && !visited.contains(curr.getNorth().getId()))
				searchQueue.add(curr.getNorth().clone().setPrev(curr).setFromPrev("north"));
			if (curr.getEast() != null && !visited.contains(curr.getEast().getId()))
				searchQueue.add(curr.getEast().clone().setPrev(curr).setFromPrev("east"));
			if (curr.getSouth() != null && !visited.contains(curr.getSouth().getId()))
				searchQueue.add(curr.getSouth().clone().setPrev(curr).setFromPrev("south"));
			if (curr.getWest() != null && !visited.contains(curr.getWest().getId()))
				searchQueue.add(curr.getWest().clone().setPrev(curr).setFromPrev("west"));
		}
		return null;
	}
	
	public static Map<String,GoXStation> GetStations() {
		return stations;
	}
	
	public static void BackupMap(String name) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmSS");
			
			File source = new File(name);
			File dest = new File(name+simpleDateFormat.format(new Date())+".backup");
			
			Files.copy(source.toPath(), dest.toPath(),
	                StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			logger.warning("An error occurred backing uo the map file! "+ e.getStackTrace());
		}
	}
	
	public static void ToFile(String name) {
		try {
		      FileWriter writer = new FileWriter(name);
		      BufferedWriter nodeWriter = new BufferedWriter(writer);
		      for (GoXNode node : nodes) {
		    	  nodeWriter.write(node.getId());
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node instanceof GoXStation ? ((GoXStation) node).GetName() : "null");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getX()+"");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getY()+"");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getZ()+"");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getNorth()!=null?node.getNorth().getId():"null");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getEast()!=null?node.getEast().getId():"null");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getSouth()!=null?node.getSouth().getId():"null");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getWest()!=null?node.getWest().getId():"null");
		    	  nodeWriter.newLine();
		      }
		      nodeWriter.close();
		      logger.info("Successfully wrote to the file.");
	    }
		catch (IOException e) {
	      logger.warning("An error occurred writing out a map to the file! "+ e.getStackTrace());
	    }
	}
	
	public static void FromFile(String name) {
		try {
            FileReader fileReader = new FileReader(name);
            
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line = null;
            
            nodes = new ArrayList<GoXNode>();
        	stations = new HashMap<String,GoXStation>();

            while((line = bufferedReader.readLine()) != null) {
            	String id = line;
            	line = bufferedReader.readLine();
            	String stationName = line;
            	line = bufferedReader.readLine();
            	int x = Integer.parseInt(line);
            	line = bufferedReader.readLine();
            	int y = Integer.parseInt(line);
            	line = bufferedReader.readLine();
            	int z = Integer.parseInt(line);
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	
            	if (!stationName.equals("null")) {
            		GoXStation newStation = new GoXStation(id, x, y, z, null, null, null, null, stationName);
            		stations.put(stationName, newStation);
            		nodes.add(newStation);
            	}
            	else {
            		GoXNode newNode = new GoXNode(id, x, y, z, null, null, null, null);
            		nodes.add(newNode);
            	}
            }   
            
            bufferedReader.close();
            
            fileReader = new FileReader(name);
            
            bufferedReader = new BufferedReader(fileReader);
            
            line = null;
            
            while((line = bufferedReader.readLine()) != null) {
            	String id = line;
            	line = bufferedReader.readLine();
            	String stationName = line;
            	line = bufferedReader.readLine();
            	int x = Integer.parseInt(line);
            	line = bufferedReader.readLine();
            	int y = Integer.parseInt(line);
            	line = bufferedReader.readLine();
            	int z = Integer.parseInt(line);
            	line = bufferedReader.readLine();
            	String northId = line;
            	line = bufferedReader.readLine();
            	String eastId = line;
            	line = bufferedReader.readLine();
            	String southId = line;
            	line = bufferedReader.readLine();
            	String westId = line;
            	GoXNode target = GetNode(id);
            	if (!northId.equals("null")) {
            		GoXNode north = GetNode(northId);
            		target.SetNorth(north);
            	}
            	if (!eastId.equals("null")) {
            		GoXNode east = GetNode(eastId);
            		target.SetEast(east);
            	}
            	if (!southId.equals("null")) {
            		GoXNode south = GetNode(southId);
            		target.SetSouth(south);
            	}
            	if (!westId.equals("null")) {
            		GoXNode west = GetNode(westId);
            		target.SetWest(west);
            	}
            	if (!stationName.equals("null")) {
            		GoXStation targetStation = stations.get(stationName);
            		if (!northId.equals("null")) {
                		GoXNode north = GetNode(northId);
                		targetStation.SetNorth(north);
                	}
                	if (!eastId.equals("null")) {
                		GoXNode east = GetNode(eastId);
                		targetStation.SetEast(east);
                	}
                	if (!southId.equals("null")) {
                		GoXNode south = GetNode(southId);
                		targetStation.SetSouth(south);
                	}
                	if (!westId.equals("null")) {
                		GoXNode west = GetNode(westId);
                		targetStation.SetWest(west);
                	}
            	}
            }
            
            bufferedReader.close();
        }
        catch(FileNotFoundException e) {
            logger.warning("Unable to open file with a map! Is it a first lauch?");
        }
        catch(IOException e) {
            logger.warning("Error reading file '" + name + "' "+ e.getStackTrace());
        }
	}
}
