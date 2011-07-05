package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntCommand extends BaseCommand {
	
	public HuntCommand()
	{
		needPlayer = true;
		permission = "monsterhunt.usercmd.hunt";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		MonsterHuntWorld world = HuntWorldManager.getWorld(((Player) sender).getWorld().getName());
		if (world == null || world.getWorld() == null) return true;
		if (world.Score.containsKey(((Player) sender).getName()))
		{
			Util.Message(world.settings.getString("Messages.MessageAlreadySignedUp"), sender);
			return true;
		}
				
		if (world.state < 2)
		{
			String message = world.settings.getString("Messages.SignupBeforeHuntMessage");
    		message = message.replace("<World>", world.name);
    		Util.Message(message, sender);
			world.Score.put(((Player) sender).getName(), 0);
			
		}
		else if (world.state == 2 && (world.getSignUpPeriodTime() == 0 || world.settings.getBoolean("AllowSignUpAfterHuntStart")))
		{
			String message = world.settings.getString("Messages.SignupAtHuntMessage");
    		message = message.replace("<World>", world.name);
    		Util.Message(message, sender);
			world.Score.put(((Player) sender).getName(), 0);
		}
		else
		{
			Util.Message(world.settings.getString("Messages.MessageTooLateSignUp"), sender);
		}
		return true;
	}

}
