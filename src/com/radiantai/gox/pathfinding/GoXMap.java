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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.PathNode;

public class GoXMap {
	private static List<GoXNode> nodes = new ArrayList<GoXNode>();
	private static Map<String, GoXStation> stations = new TreeMap<String,GoXStation>(String.CASE_INSENSITIVE_ORDER);
	private static GoX plugin;
	private static ConfigurationSection config;
	private static Logger logger;
	
	public static void SetupPlugin(GoX setPlugin, Logger setLogger) {
		plugin = setPlugin;
		logger = setLogger;
		config = plugin.getConfig().getConfigurationSection("config");
	}
	
	public static void AddNode(GoXNode node) {
		nodes.add(node);
	}
	
	public static void AddNode(Location location) throws Exception {
		GoXNode node = GoXMap.GetNode(location);
		if (node != null) {
			throw new Exception(GoXChat.chat("already location"));
		}
		if (isNearby(location)) {
			throw new Exception(GoXChat.chat("cannot touch"));
		}
		nodes.add(new GoXNode(location));
	}
	
	public static void AddStation(GoXStation station) {
		if (nodes != null) {
			nodes.add(station);
		}
	}
	
	public static void AddStation(String name, Location location) throws Exception {
		String idName = name.toLowerCase();
		GoXNode node = GoXMap.GetNode(location);
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
		if (isNearby(location)) {
			throw new Exception(GoXChat.chat("cannot touch"));
		}
		GoXStation newst = new GoXStation(name, location);
		nodes.add(newst);
		stations.put(idName, newst);
	}
	
	public static void RemoveStation(String name) throws Exception {
		if (stations != null) {
			GoXStation st = stations.get(name.toLowerCase());
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
			if (!nodes.removeIf(n -> (n.getId() == id))) {
				throw new Exception(GoXChat.chat("no such node"));
			}
			if (node.north != null) {
				node.north.unlink(id);
			}
			if (node.south != null) {
				node.south.unlink(id);
			}
			if (node.east != null) {
				node.east.unlink(id);
			}
			if (node.west != null) {
				node.west.unlink(id);
			}
		}
	}
	
	public static GoXNode GetNode(Location location) {
		List<GoXNode> list = nodes.stream()
		.filter(node -> (
				node.getLocation().getBlockX() == location.getBlockX() &&
				node.getLocation().getBlockZ() == location.getBlockZ() &&
				node.getWorld().equals(location.getWorld().getName())))
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
		GoXStation st = stations.get(name.toLowerCase());
		return st;
	}
	
	
	public static void LinkNodes(GoXNode from, GoXNode to) throws Exception {
		if (from.getId().equals(to.getId())) {
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
	
	public static void LinkNodesManual(GoXNode from, String fromDir, GoXNode to, String toDir) throws Exception {
		fromDir = fromDir.toLowerCase();
		toDir = toDir.toLowerCase();
		
		if (from.getId().equals(to.getId())) {
			throw new Exception(GoXChat.chat("to itself"));
		}
		
		from.setLink(fromDir, to);
		to.setLink(toDir, from);
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
		Queue<PathNode> openSet = new PriorityQueue<PathNode>();
		List<String> closeSet = new ArrayList<String>();
		PathNode first = new PathNode(GetNode(start));
		GoXNode finishNode = GetNode(finish);
		first.setDistance(0);
		first.setEstimated(GoXUtils.getBlockDistance(first.getCurrent().getLocation(), finishNode.getLocation()));
		openSet.add(first);
		while (!openSet.isEmpty()) {
			PathNode currNode = openSet.poll();
			GoXNode curr = currNode.getCurrent();
			int distance = currNode.getDistance();
			closeSet.add(curr.getId());
			if (curr.getId().equals(finish)) {
				return constructPath(currNode);
			}
			if (curr.getForceDirection() == null) {
				if (curr.getNorth() != null && !closeSet.contains(curr.getNorth().getId())) {
					GoXNode node = curr.getNorth();
					int addedDistance = distance + GoXUtils.getBlockDistance(curr.getLocation(), node.getLocation());
					int estimated = addedDistance + GoXUtils.getBlockDistance(node.getLocation(), finishNode.getLocation());
					PathNode pathNode = new PathNode(node, currNode, "north", addedDistance, estimated);
					openSet.add(pathNode);
				}
				if (curr.getEast() != null && !closeSet.contains(curr.getEast().getId())) {
					GoXNode node = curr.getEast();
					int addedDistance = distance + GoXUtils.getBlockDistance(curr.getLocation(), node.getLocation());
					int estimated = addedDistance + GoXUtils.getBlockDistance(node.getLocation(), finishNode.getLocation());
					PathNode pathNode = new PathNode(node, currNode, "east", addedDistance, estimated);
					openSet.add(pathNode);
				}
				if (curr.getSouth() != null && !closeSet.contains(curr.getSouth().getId())) {
					GoXNode node = curr.getSouth();
					int addedDistance = distance + GoXUtils.getBlockDistance(curr.getLocation(), node.getLocation());
					int estimated = addedDistance + GoXUtils.getBlockDistance(node.getLocation(), finishNode.getLocation());
					PathNode pathNode = new PathNode(node, currNode, "south", addedDistance, estimated);
					openSet.add(pathNode);
				}
				if (curr.getWest() != null && !closeSet.contains(curr.getWest().getId())) {
					GoXNode node = curr.getWest();
					int addedDistance = distance + GoXUtils.getBlockDistance(curr.getLocation(), node.getLocation());
					int estimated = addedDistance + GoXUtils.getBlockDistance(node.getLocation(), finishNode.getLocation());
					PathNode pathNode = new PathNode(node, currNode, "west", addedDistance, estimated);
					openSet.add(pathNode);
				}
			}
			else { //force direction
				GoXNode forcedTo = curr.getLink(curr.getForceDirection());
				if (!closeSet.contains(forcedTo.getId())) {
					int addedDistance = distance + GoXUtils.getBlockDistance(curr.getLocation(), forcedTo.getLocation());
					int estimated = addedDistance + GoXUtils.getBlockDistance(forcedTo.getLocation(), finishNode.getLocation());
					PathNode pathNode = new PathNode(forcedTo, currNode, curr.getForceDirection(), addedDistance, estimated);
					openSet.add(pathNode);
				}
			}
		}
		return null;
	}
	
	private static GoXPath constructPath(PathNode curr) {
		GoXPath path = new GoXPath();
		while (curr.getPrev() != null) {
			path.Push(curr.getFromPrev());
			curr = curr.getPrev();
		}
		return path;
	}
	
	public static Map<String,GoXStation> GetStations() {
		return stations;
	}
	
	public static void BackupMap(String path, String name) {
		try {
			File source = new File(path+name);
			if (!source.exists()) {
				new File(path).mkdirs();
				logger.warning("An error occurred backing up the map file! There is no original file: "+path+name);
				return;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmSS");
			
			File dest = new File(path+name+simpleDateFormat.format(new Date())+".backup");
			
			Files.copy(source.toPath(), dest.toPath(),
	                StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			logger.warning("An error occurred backing uo the map file! "+ e.getStackTrace());
		}
	}
	
	public static boolean isNearby(Location location) {
		Block block = location.getBlock();
		Block north = block.getRelative(BlockFace.NORTH);
		Block east = block.getRelative(BlockFace.EAST);
		Block south = block.getRelative(BlockFace.SOUTH);
		Block west = block.getRelative(BlockFace.WEST);
		if (north.getType() == Material.BRICK || north.getType() == Material.NETHERRACK) {
			if (GetNode(north.getLocation()) != null)
				return true;
		}
		if (east.getType() == Material.BRICK || east.getType() == Material.NETHERRACK) {
			if (GetNode(east.getLocation()) != null)
				return true;
				}
		if (south.getType() == Material.BRICK || south.getType() == Material.NETHERRACK) {
			if (GetNode(south.getLocation()) != null)
				return true;
		}
		if (west.getType() == Material.BRICK || west.getType() == Material.NETHERRACK) {
			if (GetNode(west.getLocation()) != null)
				return true;
		}
		return false;
	}
	
	public static void ToFile(String path, String name) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
				return;
			}
			if (nodes.isEmpty()) {
				return;
			}
			FileWriter writer = new FileWriter(path+name);
			BufferedWriter nodeWriter = new BufferedWriter(writer);
			for (GoXNode node : nodes) {
		    	  nodeWriter.write(node.getId());
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node instanceof GoXStation ? ((GoXStation) node).GetName() : "null");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getWorld()+"");
		    	  nodeWriter.newLine();
		    	  nodeWriter.write(node.getX()+",");
		    	  nodeWriter.write(node.getY()+",");
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
		    	  nodeWriter.write(node.getForceDirection()!=null?node.getForceDirection():"null");
		    	  nodeWriter.newLine();
		    	  if (node instanceof GoXStation) {
		    		  GoXStation st = (GoXStation) node;
		    		  if (st.getDropPoint()!= null) {
		    			  nodeWriter.write(st.getDropPoint().getX()+",");
			    		  nodeWriter.write(st.getDropPoint().getY()+",");
			    		  nodeWriter.write(st.getDropPoint().getZ()+",");
			    		  nodeWriter.write(st.getDropPoint().getYaw()+",");
			    		  nodeWriter.write(st.getDropPoint().getPitch()+"");
		    		  }
		    		  else {
		    			  nodeWriter.write("null");
		    		  }
		    	  }
		    	  else {
		    		  nodeWriter.write("null");
		    	  }
		    	  nodeWriter.newLine();
			}
		      	nodeWriter.close();
		      	logger.info("Successfully wrote to the file.");
	    }
		catch (IOException e) {
	      logger.warning("An error occurred writing out a map to the file! "+ e.getStackTrace());
	    }
	}
	
	public static void FromFile(String path, String name) {
		try {
			File file = new File(path+name);
			if (!file.exists()) {
				file = new File(path);
				file.mkdirs();
				return;
			}
            FileReader fileReader = new FileReader(path+name);
            
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line = null;
            
            nodes = new ArrayList<GoXNode>();
        	stations = new TreeMap<String,GoXStation>(String.CASE_INSENSITIVE_ORDER);

            while((line = bufferedReader.readLine()) != null) {
            	String id = line;
            	line = bufferedReader.readLine();
            	String stationName = line;
            	line = bufferedReader.readLine();
            	String worldName = line;
            	line = bufferedReader.readLine();
            	String[] tokens = line.split(",");
            	int x = Integer.parseInt(tokens[0]);
            	int y = Integer.parseInt(tokens[1]);
            	int z = Integer.parseInt(tokens[2]);
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	bufferedReader.readLine();
            	line = bufferedReader.readLine();
            	String forceDirection = line.equals("null") ? null : line;
            	line = bufferedReader.readLine();
            	Location locationD = null;
            	tokens = line.split(",");
            	if (!line.equals("null")) {
            		double xd = Double.parseDouble(tokens[0]);
                	double yd = Double.parseDouble(tokens[1]);
                	double zd = Double.parseDouble(tokens[2]);
                	float yaw = Float.parseFloat(tokens[3]);
                	float pitch = Float.parseFloat(tokens[4]);
                	locationD = new Location(Bukkit.getWorld(worldName),xd,yd,zd,yaw,pitch);
            	}
            	
            	Location location = new Location(Bukkit.getWorld(worldName),x,y,z);
            	
            	if (!stationName.equals("null")) {
            		GoXStation newStation = new GoXStation(id, location, null, null, null, null, stationName, forceDirection, locationD);
            		stations.put(stationName, newStation);
            		nodes.add(newStation);
            	}
            	else {
            		GoXNode newNode = new GoXNode(id, location, null, null, null, null, forceDirection);
            		nodes.add(newNode);
            	}
            }   
            
            bufferedReader.close();
            
            fileReader = new FileReader(path+name);
            
            bufferedReader = new BufferedReader(fileReader);
            
            line = null;
            
            while((line = bufferedReader.readLine()) != null) {
            	String id = line;
            	line = bufferedReader.readLine();
            	String stationName = line;
            	line = bufferedReader.readLine(); //worldName
            	line = bufferedReader.readLine(); //location
            	line = bufferedReader.readLine();
            	String northId = line;
            	line = bufferedReader.readLine();
            	String eastId = line;
            	line = bufferedReader.readLine();
            	String southId = line;
            	line = bufferedReader.readLine();
            	String westId = line;
            	line = bufferedReader.readLine(); //forceDirection
            	line = bufferedReader.readLine(); //dropPoint
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
