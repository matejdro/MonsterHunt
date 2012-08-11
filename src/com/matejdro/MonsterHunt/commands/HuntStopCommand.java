package com.matejdro.MonsterHunt.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.MonsterHunt.HuntWorldManager;
import com.matejdro.MonsterHunt.MonsterHuntWorld;
import com.matejdro.MonsterHunt.Setting;
import com.matejdro.MonsterHunt.Settings;
import com.matejdro.MonsterHunt.Util;

public class HuntStopCommand extends BaseCommand {
	
	public HuntStopCommand()
	{
		needPlayer = false;
		permission = "monsterhunt.admincmd.huntstop";
		adminCommand = true;
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1 && Settings.globals.getBoolean(Setting.HuntZoneMode.getString(), false))
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
