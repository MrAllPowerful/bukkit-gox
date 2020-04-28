package com.radiantai.gox;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.radiantai.gox.commands.GoM;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.commands.Go;
import com.radiantai.gox.listeners.GoXAddNode;
import com.radiantai.gox.listeners.GoXAddStation;
import com.radiantai.gox.listeners.GoXBreak;
import com.radiantai.gox.listeners.GoXCartDestroy;
import com.radiantai.gox.listeners.GoXCartRecycler;
import com.radiantai.gox.listeners.GoXDispenseCart;
import com.radiantai.gox.listeners.GoXLeave;
import com.radiantai.gox.listeners.GoXPathMovement;
import com.radiantai.gox.listeners.GoXPlaceCart;
import com.radiantai.gox.listeners.GoXSit;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.schedule.GoXMapBackup;

public class GoX extends JavaPlugin {
	
	private Logger bukkitLogger;
	private String mapFilePath;
	private String mapFileName;
	private Material nodeBlock;
	private Material stationBlock;
	private double cartMaxSpeed;
	private int cartTicksToLive;
	
	public void onEnable() {
		bukkitLogger = getLogger();
		
		loadConfig();
		registerEvents();
		registerCommands();
		
		mapFilePath = "plugins\\"+this.getName()+"\\Map\\";
		mapFileName = "nodes.dat";
		
		GoXMap.SetupPlugin(this, bukkitLogger);
		GoXMap.FromFile(mapFilePath, mapFileName);
		GoXChat.setupChat(this);
		
		scheduleBackup();
	}
	
	private void registerCommands() {
		getCommand("gom").setExecutor(new GoM(this));
		getCommand("go").setExecutor(new Go(this));
		
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new GoXPathMovement(this), this);
		pm.registerEvents(new GoXAddNode(this), this);
		pm.registerEvents(new GoXAddStation(this), this);
		pm.registerEvents(new GoXBreak(this), this);
		pm.registerEvents(new GoXSit(this), this);
		pm.registerEvents(new GoXLeave(this), this);
		pm.registerEvents(new GoXDispenseCart(this), this);
		pm.registerEvents(new GoXPlaceCart(this), this);
		pm.registerEvents(new GoXCartDestroy(this), this);
		pm.registerEvents(new GoXCartRecycler(this), this);
	}

	public void onDisable() {
		GoXMap.BackupMap(mapFilePath, mapFileName);
		GoXMap.ToFile(mapFilePath, mapFileName);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		cartMaxSpeed = getConfig().getConfigurationSection("config").getDouble("cart velocity multiplier");
		cartTicksToLive = getConfig().getConfigurationSection("config").getInt("empty cart ticks live");
		nodeBlock = Material.values()[getConfig().getConfigurationSection("config").getInt("node block")];
		stationBlock = Material.values()[getConfig().getConfigurationSection("config").getInt("station block")];
	}
	
	public void scheduleBackup() {
		BukkitScheduler scheduler = getServer().getScheduler();
		long backupWait = getConfig().getConfigurationSection("backup").getLong("ticks between backups");
        scheduler.runTaskTimerAsynchronously(this, new GoXMapBackup(bukkitLogger, mapFilePath, mapFileName), 300L, backupWait);
	}
	public Material getNodeBlock() {
		return nodeBlock;
	}
	public Material getStationBlock() {
		return stationBlock;
	}
	public double getCartMaxSpeed() {
		return cartMaxSpeed;
	}
	public int getCartTicksToLive() {
		return cartTicksToLive;
	}
}
