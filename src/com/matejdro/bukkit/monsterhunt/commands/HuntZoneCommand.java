package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntZoneCreation;

public class HuntZoneCommand extends BaseCommand {
	
	public HuntZoneCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.admincmd.huntzone";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		HuntZoneCreation.selectstart((Player) sender);
		return true;
	}

}
