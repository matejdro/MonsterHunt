package com.matejdro.bukkit.monsterhunt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	public static YamlConfiguration globals;
	
	private YamlConfiguration config;
	
	public Settings(YamlConfiguration cfg, File file)
	{
		config = cfg;
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int getInt(Setting setting)
	{
		Integer property = (Integer) config.get(setting.getString());
		if (property == null) property = (Integer) globals.get(setting.getString());
		
		return property;
	}
	
	public String getString(Setting setting)
	{
		String property = (String) config.get(setting.getString());
		if (property == null) property = (String) globals.get(setting.getString());
		
		return property;

	}
	
	public Boolean getBoolean(Setting setting)
	{
		Boolean property = (Boolean) config.get(setting.getString());
		if (property == null) property = (Boolean) globals.get(setting.getString());
		
		return property;

	}
	
	public int getPlaceInt(Setting setting, int place)
	{
		Integer property = (Integer) config.get(setting.getString() + String.valueOf(place));
		if (property == null) property = (Integer) globals.get(setting.getString() + String.valueOf(place));
		
		return property;
	}
	
	public String getPlaceString(Setting setting, int place)
	{
		String property = (String) config.get(setting.getString() + String.valueOf(place));
		if (property == null) property = (String) globals.get(setting.getString() + String.valueOf(place));
		
		return property;

	}
	
	public int getMonsterValue(String mobname, String killer)
	{
		String setting = "Value." + mobname + "." + killer;
		if (config.get(setting) != null)
		{
			return config.getInt(setting, 1);
		}
		else if (globals.get(setting) != null)
		{
			return globals.getInt(setting, 1);
		}
		else
		{
			setting = "Value." + mobname + ".General";
			if (config.get(setting) != null)
			{
				return config.getInt(setting, 1);
			}
			else if (globals.get(setting) != null)
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
		if (config.get(setting) != null)
		{
			return config.getString(setting);
		}
		else if (globals.get(setting) != null)
		{
			return globals.getString(setting);
		}
		else
		{
			setting = "Messages.KillMessageGeneral";
			if (config.get(setting) != null)
			{
				return config.getString(setting);
			}
			else if (globals.get(setting) != null)
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
