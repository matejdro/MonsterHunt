package com.matejdro.bukkit.monsterhunt;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.matejdro.bukkit.monsterhunt.commands.BaseCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntScoreCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntStartCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntStatusCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntStopCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntTeleCommand;
import com.matejdro.bukkit.monsterhunt.commands.HuntZoneCommand;
import com.matejdro.bukkit.monsterhunt.listeners.MonsterHuntListener;

public class MonsterHunt extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	private MonsterHuntListener entityListener;
	Timer timer;
	
	//public static HashMap<String,Integer> highscore = new HashMap<String,Integer>();
		
	public static MonsterHunt instance;
	
	private HashMap<String, BaseCommand> commands = new HashMap<String, BaseCommand>();

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		for (MonsterHuntWorld world : HuntWorldManager.getWorlds())
			world.stop();
	}

	@Override
	public void onEnable() {
	initialize();	
		
	InputOutput.LoadSettings();
	InputOutput.PrepareDB();

	
	getServer().getPluginManager().registerEvents(entityListener, this);
	log.log(Level.INFO, "[MonsterHunt] " + this + " Loaded!");
		
	commands.put("huntstart", new HuntStartCommand());
	commands.put("huntstop", new HuntStopCommand());
	commands.put("hunt", new HuntCommand());
	commands.put("huntscore", new HuntScoreCommand());
	commands.put("huntstatus", new HuntStatusCommand());
	commands.put("huntzone", new HuntZoneCommand());
	commands.put("hunttele", new HuntTeleCommand());
	
	InputOutput.initMetrics();
	
	HuntWorldManager.timer();

	}
	
	private void initialize()
	{
		entityListener = new MonsterHuntListener();
		instance = this;
		
	}
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	BaseCommand cmd = commands.get(command.getName().toLowerCase());
    	if (cmd != null) return cmd.execute(sender, args);
    	return false;
    }
    	

}
