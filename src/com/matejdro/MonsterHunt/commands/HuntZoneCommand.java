package com.matejdro.MonsterHunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.MonsterHunt.HuntZoneCreation;

public class HuntZoneCommand extends BaseCommand {
	
	public HuntZoneCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.admincmd.huntzone";
		adminCommand = true;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		HuntZoneCreation.selectstart((Player) sender);
		return true;
	}

}
