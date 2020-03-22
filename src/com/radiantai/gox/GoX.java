package com.radiantai.gox;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.radiantai.gox.commands.GoM;
import com.radiantai.gox.commands.Go;
import com.radiantai.gox.listeners.GoXAddNode;
import com.radiantai.gox.listeners.GoXAddStation;
import com.radiantai.gox.listeners.GoXBreak;
import com.radiantai.gox.listeners.GoXMovement;
import com.radiantai.gox.listeners.GoXSit;
import com.radiantai.gox.pathfinding.GoMap;

public class GoX extends JavaPlugin {
	private PluginDescriptionFile pdf;
	private Logger logger;
	private Logger bukkitLogger;
	public void onEnable() {
		pdf = getDescription();
		logger = Logger.getLogger("Minecraft");
		bukkitLogger = getLogger();
		
		getCommand("gom").setExecutor(new GoM(this));
		getCommand("go").setExecutor(new Go(this));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new GoXMovement(this), this);
		pm.registerEvents(new GoXAddNode(this), this);
		pm.registerEvents(new GoXAddStation(this), this);
		pm.registerEvents(new GoXBreak(this), this);
		pm.registerEvents(new GoXSit(this), this);
		
		GoMap.FromFile("plugins\\Gox\\nodes.txt");
	}
	
	public void onDisable() {
		GoMap.ToFile("plugins\\Gox\\nodes.txt");
	}
}
