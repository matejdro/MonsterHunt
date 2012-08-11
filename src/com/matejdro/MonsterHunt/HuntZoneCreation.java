package com.matejdro.MonsterHunt;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HuntZoneCreation {
public static HashMap<String,CreationPlayer> players = new HashMap<String,CreationPlayer>();

	
	public static void selectstart(Player player)
	{
		if (players.containsKey(player.getName()))
		{
			players.remove(player.getName());
		}
		if (!player.getInventory().contains(Settings.globals.getInt(Setting.SelectionTool.getString(), 268)))
			player.getInventory().addItem(new ItemStack(Settings.globals.getInt(Setting.SelectionTool.getString(), 268),1));
		
		Util.Message("§Hunt Zone Selection:", player);
		Util.Message("First, you must select hunt zone cuboid. Select first point of the cuboid by right clicking on the block with your wooden sword. DO NOT FORGET TO MARK FLOOR AND CEILING TOO!", player);
		players.put(player.getName(), new CreationPlayer());
	}
	
	public static void select(Player player, Block block)
	{
		switch (players.get(player.getName()).state)
		{
			case 1:
				firstpoint(player,block);
				break;
			case 2:
				secondpoint(player,block);
				break;
			case 3:
				telepoint(player);
				break;			
			
		}
	}
	
	private static void firstpoint(Player player, Block block)
	{
		Util.Message("First point selected. Now select second point.", player);
		CreationPlayer cr = players.get(player.getName());
		cr.corner1 = block.getLocation();
		cr.state++;
		
	}

	private static void secondpoint(Player player, Block block)
	{
		Util.Message("Second point selected. Now go inside zone and right click anywhere to select your current position as teleport location.", player);
		CreationPlayer cr = players.get(player.getName());
		cr.corner2 = block.getLocation();
		cr.state++;
		
	}
		
	
	private static void telepoint(Player player)
	{
		
		CreationPlayer cr = players.get(player.getName());
		cr.teleloc = player.getLocation();
		cr.state++;
		
		HuntZone.corner1 = cr.corner1;
		HuntZone.corner2 = cr.corner2;
		HuntZone.teleport = cr.teleloc;
		InputOutput.saveZone();
		
		Util.Message("Hunt Zone set successfully!", player);
		
		
		
		players.remove(player.getName());
	}

	
	private static class CreationPlayer
	{
		public int state;
		
		public Location corner1;
		public Location corner2;
		public Location teleloc;
		
		public CreationPlayer()
		{
			state = 1;
		}
}

}
