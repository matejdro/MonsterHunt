package com.matejdro.MonsterHunt.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.MonsterHunt.HuntWorldManager;
import com.matejdro.MonsterHunt.MonsterHuntWorld;
import com.matejdro.MonsterHunt.Util;

public class HuntStartCommand extends BaseCommand {
	
	public HuntStartCommand()
	{
		needPlayer = false;
		permission = "monsterhunt.admincmd.huntstart";
		adminCommand = true;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1 && HuntWorldManager.getWorlds().size() == 1)
		{
			String name = "";
			for (MonsterHuntWorld w : HuntWorldManager.getWorlds())
				name = w.name;
			args = new String[]{name};
		}
		else if (args.length < 1)
		{
			Util.Message("Usage: /huntstart [World Name]",sender);
			return true;
		}
		else if (HuntWorldManager.getWorld(args[0]) == null)
		{
			Util.Message("There is no such world!", sender);
			return true;
		}
		MonsterHuntWorld world = HuntWorldManager.getWorld(args[0]);
		world.start();
		world.manual = true;
		return true;
	}

}
