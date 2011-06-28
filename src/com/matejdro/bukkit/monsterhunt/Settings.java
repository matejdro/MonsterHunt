package com.matejdro.bukkit.monsterhunt;

import java.util.HashMap;

import org.bukkit.util.config.Configuration;

public class Settings {
	public static HashMap<String, Object> defaults = new HashMap<String, Object>();
	public static Configuration globals;
	
	private Configuration config;
	
	public Settings(Configuration cfg)
	{
		config = cfg;
		config.load();
	}
	
	
	public int getInt(String setting)
	{
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
			return (Integer) defaults.get(setting);
		}
	}
	
	public String getString(String setting)
	{
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
			return (String) defaults.get(setting);
		}
	}
	
	public Boolean getBoolean(String setting)
	{
		if (config.getProperty(setting) != null)
		{
			return config.getBoolean(setting, false);
		}
		if (globals.getProperty(setting) != null)
		{
			return globals.getBoolean(setting, false);
		}
		else
		{
			return (Boolean) defaults.get(setting);
		}
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
				return (Integer) defaults.get(setting);
			}

		}
		
	}
	
	public String getKillMessage(String cause)
	{
		String setting = "Messages.KillMessage" + cause;
		MonsterHunt.Debug(setting);
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
				return (String) defaults.get(setting);
			}
		}
	}


					
}
