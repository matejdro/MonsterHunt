package com.matejdro.bukkit.monsterhunt.commands;

import java.util.LinkedHashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.InputOutput;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntScoreCommand extends BaseCommand {
	
	public HuntScoreCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.usercmd.huntscore";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		
		
		if (args.length > 0 && args[0].equals("rank"))
		{
			Integer rank = InputOutput.getHighScoreRank(((Player) sender).getName());
			if (rank != null)
				Util.Message("Your current high score rank is " + String.valueOf(rank), sender);
			else
				Util.Message("You do not have your high score yet.", sender);
		}
		else if (args.length > 0 && args[0].equals("top"))
		{
			Integer number = 5;
			if (args.length > 1) number = Integer.parseInt(args[1]);
			
			LinkedHashMap<String, Integer> tops = InputOutput.getTopScores(number);
			Util.Message("Top high scores:", sender);
			int counter = 0;
			for (String player : tops.keySet())
			{
				counter++;
				String rank = String.valueOf(counter);
				String score = String.valueOf(tops.get(player));
				
				Util.Message(rank + ". &6" + player + "&f - &a" + score + "&f points", sender);
			}
		}
		else if (args.length > 0)
		{
			Integer score = InputOutput.getHighScore(args[0]);
			if (score != null)
				Util.Message("High score of player &6" + args[0] + "&f is &6" + String.valueOf(score) + "&f points.", sender);
			else
				Util.Message("Player &6" + args[0] + "&f do not have high score yet.", sender);
		}
		else
		{
			Integer score = InputOutput.getHighScore(((Player) sender).getName());
			if (score != null)
				Util.Message("Your high score is &6" + String.valueOf(score) + "&f points.", sender);
			else
				Util.Message("You do not have your high score yet.", sender);
		}
		
		return true;
	}

}
