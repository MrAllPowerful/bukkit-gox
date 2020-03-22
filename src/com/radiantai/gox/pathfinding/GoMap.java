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

public class GoMap {
	private static List<GoNode> nodes = new ArrayList<GoNode>();
	private static Map<String, GoStation> stations = new HashMap<String,GoStation>();
	private static GoX plugin;
	private static ConfigurationSection config;
	private static Logger logger;
	
	public static void SetupPlugin(GoX setPlugin, Logger setLogger) {
		plugin = setPlugin;
		logger = setLogger;
		config = plugin.getConfig().getConfigurationSection("lang").getConfigurationSection("other");
	}
	
	public static void AddNode(GoNode node) {
		nodes.add(node);
	}
	
	public static void AddNode(int x, int y, int z) throws Exception {
		GoNode node = GoMap.GetNode(x, z);
		if (node != null) {
			throw new Exception(config.getString("already location"));
		}
		nodes.add(new GoNode(x, y, z));
	}
	
	public static void AddStation(GoStation station) {
		if (nodes != null) {
			nodes.add(station);
		}
	}
	
	public static void AddStation(String name, int x, int y, int z) throws Exception {
		GoNode node = GoMap.GetNode(x, z);
		if (node != null) {
			throw new Exception(config.getString("already location"));
		}
		if (stations.get(name) != null) {
			throw new Exception(config.getString("already name"));
		}
		GoStation newst = new GoStation(name, x, y, z);
		nodes.add(newst);
		stations.put(name, newst);
	}
	
	public static void RemoveStation(String name) throws Exception {
		if (stations != null) {
			GoStation st = stations.get(name);
			if (st == null) {
				throw new Exception(config.getString("no such station"));
			}
			RemoveNodeP(st.getId());
			stations.remove(name);
		}
	}
	
	public static void RemoveNode(String id) throws Exception {
		GoNode node = GetNode(id);
		if (node instanceof GoStation) {
			throw new Exception(config.getString("cannot remove node"));
		}
		RemoveNodeP(id);
	}
	
	private static void RemoveNodeP(String id) throws Exception {
		if (nodes != null) {
			GoNode node = GetNode(id);
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
				throw new Exception(config.getString("no such node"));
			}
		}
	}
	
	public static GoNode GetNode(int x, int z) {
		List<GoNode> list = nodes.stream()
		.filter(node -> (node.getX() == x && node.getZ() == z))
		.collect(Collectors.toList());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public static GoNode GetNode(String id) {
		List<GoNode> list = nodes.stream()
		.filter(node -> (node.getId().equals(id)))
		.collect(Collectors.toList());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public static GoStation GetStation(String name) {
		GoStation st = stations.get(name);
		return st;
	}
	
	
	public static void LinkNodes(GoNode from, GoNode to) throws Exception {
		if (from.getX()==to.getX() && from.getZ()==to.getZ()) {
			throw new Exception(config.getString("to itself"));
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
			throw new Exception(config.getString("one line"));
		}
	}
	
	public static void MessageNodes(Player player) {
		player.sendMessage(ChatColor.AQUA+config.getString("nodes"));
		if (!nodes.isEmpty()) {
			for (GoNode node : nodes) {
				player.sendMessage(ChatColor.GREEN + " "+node.toString());
			}
		}
		else {
			player.sendMessage(ChatColor.YELLOW+"<"+config.getString("empty")+">");
		}
	}
	
	public static void MessageStations(Player player) {
		player.sendMessage(ChatColor.AQUA+config.getString("stations"));
		if (!nodes.isEmpty()) {
			for (GoStation st : new ArrayList<GoStation>(stations.values())) {
				player.sendMessage(ChatColor.GREEN + " "+st.toString());
			}
		}
		else {
			player.sendMessage(ChatColor.RED+"<"+config.getString("empty")+">");
		}
	}
	
	public static GoPath FindPath(String start, String finish) {
		Queue<GoNode> searchQueue = new LinkedList<GoNode>();
		ArrayList<String> visited = new ArrayList<String>();
		GoNode first = GetNode(start).clone();
		GoPath path = new GoPath();
		searchQueue.add(first);
		while (!searchQueue.isEmpty()) {
			GoNode curr = searchQueue.poll();
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
		      for (GoNode node : nodes) {
		    	  nodeWriter.write(node.getId());
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node instanceof GoStation ? ((GoStation) node).GetName() : "null");
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
            
            nodes = new ArrayList<GoNode>();
        	stations = new HashMap<String,GoStation>();

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
            		GoStation newStation = new GoStation(id, x, y, z, null, null, null, null, stationName);
            		stations.put(stationName, newStation);
            		nodes.add(newStation);
            	}
            	else {
            		GoNode newNode = new GoNode(id, x, y, z, null, null, null, null);
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
            	GoNode target = GetNode(id);
            	if (!northId.equals("null")) {
            		GoNode north = GetNode(northId);
            		target.SetNorth(north);
            	}
            	if (!eastId.equals("null")) {
            		GoNode east = GetNode(eastId);
            		target.SetEast(east);
            	}
            	if (!southId.equals("null")) {
            		GoNode south = GetNode(southId);
            		target.SetSouth(south);
            	}
            	if (!westId.equals("null")) {
            		GoNode west = GetNode(westId);
            		target.SetWest(west);
            	}
            	if (!stationName.equals("null")) {
            		GoStation targetStation = stations.get(stationName);
            		if (!northId.equals("null")) {
                		GoNode north = GetNode(northId);
                		targetStation.SetNorth(north);
                	}
                	if (!eastId.equals("null")) {
                		GoNode east = GetNode(eastId);
                		targetStation.SetEast(east);
                	}
                	if (!southId.equals("null")) {
                		GoNode south = GetNode(southId);
                		targetStation.SetSouth(south);
                	}
                	if (!westId.equals("null")) {
                		GoNode west = GetNode(westId);
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
