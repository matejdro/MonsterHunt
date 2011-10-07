package com.matejdro.bukkit.monsterhunt;

import java.util.HashMap;

import org.bukkit.util.config.Configuration;

public class Settings {
	public static Configuration globals;
	
	private Configuration config;
	
	public Settings(Configuration cfg)
	{
		config = cfg;
		config.load();
	}
	
	
	public int getInt(Setting setting)
	{
		Integer property = (Integer) config.getProperty(setting.getString());
		if (property == null) property = (Integer) globals.getProperty(setting.getString());
		
		return property;
	}
	
	public String getString(Setting setting)
	{
		String property = (String) config.getProperty(setting.getString());
		if (property == null) property = (String) globals.getProperty(setting.getString());
		
		return property;

	}
	
	public Boolean getBoolean(Setting setting)
	{
		Boolean property = (Boolean) config.getProperty(setting.getString());
		if (property == null) property = (Boolean) globals.getProperty(setting.getString());
		
		return property;

	}
	
	public int getPlaceInt(Setting setting, int place)
	{
		Integer property = (Integer) config.getProperty(setting.getString() + String.valueOf(place));
		if (property == null) property = (Integer) globals.getProperty(setting.getString() + String.valueOf(place));
		
		return property;
	}
	
	public String getPlaceString(Setting setting, int place)
	{
		String property = (String) config.getProperty(setting.getString() + String.valueOf(place));
		if (property == null) property = (String) globals.getProperty(setting.getString() + String.valueOf(place));
		
		return property;

	}
	
	public int getMonsterValue(String mobname, String killer)
	{
		String setting = "Value." + mobname + "." + killer;
		if (config.getProperty(setting) != null)
		{
			return config.getInt(setting, 1);
		}
		else if (globals.getProperty(setting) != null)
		{
			return globals.getInt(setting, 1);
		}
		else
		{
			setting = "Value." + mobname + ".General";
			if (config.getProperty(setting) != null)
			{
				return config.getInt(setting, 1);
			}
			else if (globals.getProperty(setting) != null)
			{
				return globals.getInt(setting, 1);
			}
			else
			{
				return 0;
			}

		}
		
	}
	
	public String getKillMessage(String cause)
	{
		String setting = "Messages.KillMessage" + cause;
		Util.Debug(setting);
		if (config.getProperty(setting) != null)
		{
			return config.getString(setting);
		}
		else if (globals.getProperty(setting) != null)
		{
			return globals.getString(setting);
		}
		else
		{
			setting = "Messages.KillMessageGeneral";
			if (config.getProperty(setting) != null)
			{
				return config.getString(setting);
			}
			else if (globals.getProperty(setting) != null)
			{
				return globals.getString(setting);
			}
			else
			{
				return "";
			}
		}
	}


					
}
