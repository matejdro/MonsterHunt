package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.World;

public class MonsterHuntWorld {
	public String name;
	public Boolean manual;
	public int state;
	public Boolean waitday;
	public int curday;
	public Boolean nextnight;
	public Settings settings;

	public HashMap<String,Integer> Score = new HashMap<String,Integer>();
	public HashMap<String,Integer> lastScore = new HashMap<String,Integer>();
	public ArrayList<Integer> properlyspawned = new ArrayList<Integer>();
	public ArrayList<Integer> blacklist = new ArrayList<Integer>();
	
	public MonsterHuntWorld(String w)
	{
		state = 0;
		waitday = false;
		manual = false;
		curday = 0;
		name = w;
	}
	
	public World getWorld()
	{
		return MonsterHunt.instance.getServer().getWorld(name);
	}
	
	public int getSignUpPeriodTime()
	{
		int time = settings.getInt("SignUpPeriodTime");
			if (time != 0)
			{
				time = settings.getInt("StartTime") - settings.getInt("SignUpPeriodTime") * 1200;
				if (time < 0)
				{
					MonsterHunt.log.log(Level.WARNING, "[MonterHunt] Wrong SignUpPeriodTime Configuration! Sign Up period will be disabled!");
					time = 0;
				}
				
			}
			
			return time;

	}

}
