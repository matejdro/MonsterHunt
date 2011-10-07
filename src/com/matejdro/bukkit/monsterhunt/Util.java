package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijikokun.bukkit.Permissions.Permissions;

public class Util {
	public static void Message(String message, CommandSender sender)
	{
		if (sender instanceof Player)
		{
			Message(message, (Player) sender);
		}
		else
		{
			sender.sendMessage(message);
		}
	}
	
	public static void Message(String message, Player player)
	{
		String color = "\u00A7f";
		message = message.replaceAll("(&([a-z0-9]))", "§$2").replace("\\\\\u00A7", "&");
		final int maxLength = 61; //Max length of chat text message
        final String newLine = "[NEWLINE]";
        ArrayList<String> chat = new ArrayList<String>();
        chat.add(0, color);
        String[] words = message.split(" ");
        int lineNumber = 0;
        for (int i = 0; i < words.length; i++) {
                if (chat.get(lineNumber).length() + words[i].length() < maxLength && !words[i].equals(newLine)) {
                        chat.set(lineNumber, chat.get(lineNumber) + " " + words[i]);
                }
                else {
                        lineNumber++;
                        if (!words[i].equals(newLine)) {
                                chat.add(lineNumber, color + words[i]);
                        }
                        else
                                chat.add(lineNumber,color);
                }
        }
        for (int i = 0; i < chat.size(); i++) {
                player.sendMessage(chat.get(i));
        }
	}
	public static void Broadcast(String message)
	{
		for (Player i : MonsterHunt.instance.getServer().getOnlinePlayers())
		{
			Message(message,i);
		}
			
	}
	
	public static void Debug(String message)
	{
		if (Settings.globals.getBoolean(Setting.Debug.getString(), false))
			MonsterHunt.log.info("[MonsterHunt][Debug]" + message);
	}
	
    public void StartFailed(MonsterHuntWorld world)
    {
    	if (world.settings.getInt(Setting.SkipToIfFailsToStart) >= 0)
    	{
    		world.getWorld().setTime(world.settings.getInt(Setting.SkipToIfFailsToStart));
    	}
    }
        
    public static Boolean permission(Player player, String line, Boolean def)
    {
    	    if(MonsterHunt.permissions != null) {
    	    	return (((Permissions) MonsterHunt.permissions).getHandler()).has(player, line);
    	    } else {
    	    	return def;
    	    }
    }

}
