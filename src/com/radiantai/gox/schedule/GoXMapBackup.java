package com.radiantai.gox.schedule;

import java.util.logging.Logger;

import com.radiantai.gox.GoX;
import com.radiantai.gox.pathfinding.GoXMap;

public class GoXMapBackup implements Runnable {
	
	private GoX plugin;
	private String fileName;
	private Logger logger;

    public GoXMapBackup(GoX plugin, Logger logger, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.logger = logger;
    }

    @Override
    public void run() {
    	logger.info("Running scheduled map backup...");
    	GoXMap.BackupMap(fileName);
		GoXMap.ToFile(fileName);
		logger.info("Backup complete!");
    }
}
