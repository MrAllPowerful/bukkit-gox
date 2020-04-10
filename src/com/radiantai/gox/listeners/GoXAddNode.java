package com.radiantai.gox.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.radiantai.gox.GoX;
import com.radiantai.gox.chat.GoXChat;
import com.radiantai.gox.structures.GoXPlayer;
import com.radiantai.gox.structures.GoXRail;

public class GoXAddNode implements Listener {
	private GoX plugin;
	
	public GoXAddNode(GoX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRailPlaced(BlockPlaceEvent e){
		Block block = e.getBlock();
		if (!GoXRail.isRails(block)) {
			return;
		}
		Block under = block.getRelative(BlockFace.DOWN);
		Player player = e.getPlayer();
		if (under.getType() == plugin.getNodeBlock()) {
			new GoXPlayer(player, plugin).setAddNode(under.getLocation());
			player.sendMessage(ChatColor.GREEN + GoXChat.chat("enter add node") + ChatColor.WHITE + "/gom add");
		}
	}
}
