package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Settings;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntStatusCommand extends BaseCommand {
	
	public HuntStatusCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.usercmd.huntstatus";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		Boolean anyactive = false;
		String actives = "";
		for (MonsterHuntWorld world : HuntWorldManager.getWorlds())
		{
			if (world.state > 0)
			{
				anyactive = true;
				actives += world.name + ",";
			}
		}
		if (!anyactive)
		{
			Util.Message(Settings.globals.getString("Messages.MessageHuntStatusNotActive"), player);
		}
		else
		{
			Util.Message(Settings.globals.getString("Messages.MessageHuntStatusHuntActive").replace("<Worlds>", actives.substring(0, actives.length()-1)) , player);
		}
		MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
		if (world == null || world.getWorld() == null) return true;
		
		if (world.state == 0)
		{
			
			if (world.lastScore.containsKey(player.getName()))
				Util.Message(world.settings.getString("Messages.MessageHuntStatusLastScore").replace("<Points>", String.valueOf(world.lastScore.get(player.getName()))),player);
			else
				Util.Message(world.settings.getString("Messages.MessageHuntStatusNotInvolvedLastHunt"),player);
		}
		else if (world.state == 2)
		{
			if (world.Score.containsKey(player.getName()))
			{
					if (world.Score.get(player.getName()) == 0 )
						Util.Message(world.settings.getString("Messages.MessageHuntStatusNoKills"), player);
					else
						Util.Message(world.settings.getString("Messages.MessageHuntStatusCurrentScore").replace("<Points>", String.valueOf(world.Score.get(player.getName()))), player);
			}
			if (world.settings.getBoolean("TellTime") && !world.manual)
			{
				int timediff = world.settings.getInt("EndTime")-world.settings.getInt("StartTime");
				long time = player.getWorld().getTime();
				long curdiff = (time - world.settings.getInt("StartTime")) * 100;
				double calc = curdiff/timediff;
				int curpercent = (int) (100 - Math.round(calc));
				curpercent +=  100;
				curpercent /= 1;
				Util.Message(world.settings.getString("Messages.MessageHuntStatusTimeReamining").replace("<Timeleft>", String.valueOf(curpercent)) , player);
			}
			
			
		}
		return true;
	}

}
