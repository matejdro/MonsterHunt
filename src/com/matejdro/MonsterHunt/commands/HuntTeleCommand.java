package com.matejdro.MonsterHunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.matejdro.MonsterHunt.HuntWorldManager;
import com.matejdro.MonsterHunt.HuntZone;
import com.matejdro.MonsterHunt.MonsterHuntWorld;
import com.matejdro.MonsterHunt.Setting;
import com.matejdro.MonsterHunt.Settings;
import com.matejdro.MonsterHunt.Util;

public class HuntTeleCommand extends BaseCommand {
	
	public HuntTeleCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.usercmd.hunttele";
		adminCommand = false;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
		if (!Settings.globals.getBoolean(Setting.HuntZoneMode.getString(), false) || world == null || world.getWorld() == null) return false;
		
		Boolean permission = !Util.permission(player, "monsterhunt.noteleportrestrictions", PermissionDefault.OP);
		
		if (world.state == 0 && permission)
		{
				Util.Message(world.settings.getString(Setting.MessageHuntTeleNoHunt),player);
				return true;
		}
		else if (world.Score.containsKey(player.getName()) && world.settings.getBoolean(Setting.EnableSignup) && permission)
		{
			Util.Message(world.settings.getString(Setting.MessageHuntTeleNotSignedUp),player);
			return true;
		}

		world.tplocations.put(player, player.getLocation());
		player.teleport(HuntZone.teleport);
		return true;
	}

}
