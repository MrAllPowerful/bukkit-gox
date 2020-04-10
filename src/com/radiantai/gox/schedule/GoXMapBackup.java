package com.radiantai.gox.schedule;

import java.util.logging.Logger;

import com.radiantai.gox.pathfinding.GoXMap;

public class GoXMapBackup implements Runnable {
	
	private String fileName;
	private String filePath;
	private Logger logger;

    public GoXMapBackup(Logger logger, String filePath, String fileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.logger = logger;
    }

    @Override
    public void run() {
    	logger.info("Running scheduled map backup...");
    	GoXMap.BackupMap(filePath, fileName);
		GoXMap.ToFile(filePath, fileName);
		logger.info("Backup complete!");
    }
}
