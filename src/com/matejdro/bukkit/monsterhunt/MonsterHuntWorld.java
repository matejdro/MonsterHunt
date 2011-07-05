package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
	public HashMap<Player, Location> tplocations = new HashMap<Player, Location>();
	
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
	
	public void start()
	{
		String message = settings.getString("Messages.StartMessage");
		message = message.replace("<World>", name);
		Util.Broadcast(message);
		state = 2;
		waitday = true;
	}
	public void stop()
	{
		if (state < 2) return;
		if (Score.size() < settings.getInt("MinimumPlayers"))
		{
			String message = settings.getString("Messages.FinishMessageNotEnoughPlayers");
			message = message.replace("<World>", name);
			Util.Broadcast(message);
		}
		else
		{
			RewardManager.RewardWinners(this);
		}
		for (Entry<Player, Location> e : tplocations.entrySet())
		{
			Player player = e.getKey();
			if (player == null || !player.isOnline()) continue;
			player.teleport(e.getValue());
		}
		state = 0;
		if (Settings.globals.getBoolean("EnableHighScores", false))
		{
			for (String i : Score.keySet())
			{
				Integer hs = InputOutput.getHighScore(i);
				if (hs == null) hs = 0;
				int score = Score.get(i);
				if (score > hs)
				{
					InputOutput.UpdateHighScore(i, score);
					Player player = MonsterHunt.instance.getServer().getPlayer(i);
					if (player != null) 
					{
						String message = settings.getString("Messages.HighScoreMessage");
						message = message.replace("<Points>", String.valueOf(score));
						Util.Message(message, player);
					}
				}
			}
		}
		
		lastScore.putAll(Score);
		Score.clear();
		properlyspawned.clear();
	}
	
	public void skipNight()
    {
     if (settings.getInt("SkipToIfFailsToStart") >= 0)
     {
    	 getWorld().setTime(settings.getInt("SkipToIfFailsToStart"));
     }
    }
	
	public Boolean canStart()
    {
    	if (curday == 0)
  	  {	
  		curday = settings.getInt("SkipDays");
  		if ((new Random().nextInt(100)) < settings.getInt("StartChance"))
		  {
			return true;
		  }
  		
  	  } else {
  		curday--;  
  	  }
    	return false;
    }

}
