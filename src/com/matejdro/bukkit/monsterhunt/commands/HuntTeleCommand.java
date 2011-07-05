package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.HuntZone;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Settings;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntTeleCommand extends BaseCommand {
	
	public HuntTeleCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.usercmd.hunttele";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
		if (!Settings.globals.getBoolean("HuntZoneMode", false) || world == null || world.getWorld() == null) return false;
		
		Boolean permission = !Util.permission(player, "monsterhunt.noteleportrestrictions", player.isOp());
		
		if (world.state == 0 && permission)
		{
				Util.Message(world.settings.getString("Messages.MessageHuntTeleNoHunt"),player);
				return true;
		}
		else if (world.Score.containsKey(player.getName()) && world.settings.getBoolean("EnableSignup") && permission)
		{
			Util.Message(world.settings.getString("Messages.MessageHuntTeleNotSignedUp"),player);
			return true;
		}

		world.tplocations.put(player, player.getLocation());
		player.teleport(HuntZone.teleport);
		return true;
	}

}
