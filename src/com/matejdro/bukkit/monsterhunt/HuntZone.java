package com.matejdro.bukkit.monsterhunt;

import org.bukkit.Location;

public class HuntZone {
	public static Location corner1;
	public static Location corner2;
	public static Location teleport;
	
	public static Boolean isInsideZone(Location loc)
	{
		if (!Settings.globals.getBoolean(Setting.HuntZoneMode.getString(), false)) return true;
		
		if (HuntZone.teleport.getWorld() == loc.getWorld() && isBetween(HuntZone.corner1.getX(),HuntZone.corner2.getX(), loc.getX()) && isBetween(HuntZone.corner1.getY(),HuntZone.corner2.getY(), loc.getY()) && isBetween(HuntZone.corner1.getZ(),HuntZone.corner2.getZ(), loc.getZ()) )
			return true;
		return false;
	}
	
	private static Boolean isBetween(double x, double y, double n)
	{
		if ((x < y) && x <= n && y >= n )
			return true;
		else if ((x > y) && x >= n && y <= n )
			return true;
		return false;	
	}
}
