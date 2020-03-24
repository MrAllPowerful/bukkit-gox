package com.radiantai.gox;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.radiantai.gox.commands.GoM;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.collisioncanceling.GoXCancelOnMove;
import com.radiantai.gox.collisioncanceling.GoXCollision;
import com.radiantai.gox.commands.Go;
import com.radiantai.gox.listeners.GoXAddNode;
import com.radiantai.gox.listeners.GoXAddStation;
import com.radiantai.gox.listeners.GoXBreak;
import com.radiantai.gox.listeners.GoXLeave;
import com.radiantai.gox.listeners.GoXMovement;
import com.radiantai.gox.listeners.GoXSit;
import com.radiantai.gox.pathfinding.GoXMap;
import com.radiantai.gox.schedule.GoXMapBackup;

public class GoX extends JavaPlugin {
	
	private PluginDescriptionFile pdf;
	private Logger bukkitLogger;
	private String mapFilePath;
	private String mapFileName;
	
	public void onEnable() {
		pdf = getDescription();
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
		pm.registerEvents(new GoXMovement(this, bukkitLogger), this);
		pm.registerEvents(new GoXAddNode(this, bukkitLogger), this);
		pm.registerEvents(new GoXAddStation(this, bukkitLogger), this);
		pm.registerEvents(new GoXBreak(this, bukkitLogger), this);
		pm.registerEvents(new GoXSit(this, bukkitLogger), this);
		//pm.registerEvents(new GoXCollision(this, bukkitLogger), this);
		//pm.registerEvents(new GoXCancelOnMove(this, bukkitLogger), this);
		pm.registerEvents(new GoXLeave(this, bukkitLogger), this);
	}

	public void onDisable() {
		GoXMap.BackupMap(mapFilePath, mapFileName);
		GoXMap.ToFile(mapFilePath, mapFileName);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void scheduleBackup() {
		BukkitScheduler scheduler = getServer().getScheduler();
		long backupWait = getConfig().getConfigurationSection("backup").getLong("ticks between backups");
        scheduler.runTaskTimerAsynchronously(this, new GoXMapBackup(this, bukkitLogger, mapFilePath, mapFileName), 300L, backupWait);
	}
}
