package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Settings;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntStopCommand extends BaseCommand {
	
	public HuntStopCommand()
	{
		needPlayer = false;
		permission = "monsterhunt.command.huntstop";
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1 && Settings.globals.getBoolean("HuntZoneMode", false))
		{
			args = new String[]{"something"};
		}
		else if (args.length < 1)
		{
			Util.Message("Usage: /huntstop [World Name]",sender);
			return true;
		}
		else if (HuntWorldManager.getWorld(args[0]) == null)
		{
			Util.Message("There is no such world!", sender);
			return true;
		}
		MonsterHuntWorld world = HuntWorldManager.getWorld(args[0]);
		world.stop();
		world.manual = false;
		world.waitday = true;
		return true;
	}

}
