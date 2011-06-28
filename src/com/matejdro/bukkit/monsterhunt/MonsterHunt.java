package com.matejdro.bukkit.monsterhunt;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

import listeners.MonsterHuntEntityListener;
import listeners.MonsterHuntPlayerListener;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.herowebhosting.feverdream.extendedday.ExtendedDay;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MonsterHunt extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	private MonsterHuntEntityListener EntityListener;
	private MonsterHuntPlayerListener PlayerListener;
	Timer timer;
	
	public static HashMap<String,Integer> highscore = new HashMap<String,Integer>();
	public static HashMap<String,MonsterHuntWorld> worlds = new HashMap<String,MonsterHuntWorld>();
	
	public static Plugin permissions = null;
	
	public static MonsterHunt instance;	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		for (MonsterHuntWorld world : worlds.values())
			world.stop();
	}

	@Override
	public void onEnable() {
	initialize();	
		
	InputOutput.LoadSettings();
	InputOutput.PrepareDB();
	if (Settings.globals.getBoolean("EnableHighScores", false)) InputOutput.LoadHighScores();
	
	
	
		
	getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, EntityListener, Event.Priority.Monitor, this);
	getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, EntityListener, Event.Priority.Monitor, this);
	getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, EntityListener, Event.Priority.Monitor, this);
	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, PlayerListener, Event.Priority.Monitor, this);
	log.log(Level.INFO, "[MonsterHunt] MonsterHunt Loaded!");

	permissions = this.getServer().getPluginManager().getPlugin("Permissions");
	
	HuntWorldManager.timer();

	}
	
	private void initialize()
	{
		EntityListener = new MonsterHuntEntityListener(this);
		PlayerListener = new MonsterHuntPlayerListener();
		instance = this;
		
	}
	
    
    
    
    
        
   
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equalsIgnoreCase("huntstart")  )
		{
			if (sender instanceof Player && !Util.permission((Player) sender, "monsterhunt.admincmd.huntstart", true)) return true; 
			
			if (args.length < 1)
			{
				Util.Message("Usage: /huntstart [World Name]",sender);
				return true;
			}
			else if (!worlds.containsKey(args[0]))
			{
				Util.Message("There is no such world!", sender);
				return true;
			}
			worlds.get(args[0]).start();
		}

		else if (command.getName().equalsIgnoreCase("huntstop"))
		{
			if (sender instanceof Player && !Util.permission((Player) sender, "monsterhunt.admincmd.huntstop", true)) return true; 
			
			if (args.length < 1)
			{
				Util.Message("Usage: /huntstop [World Name]",sender);
				return true;
			}
			else if (!worlds.containsKey(args[0]))
			{
				Util.Message("There is no such world!", sender);
				return true;
			}
			worlds.get(args[0]).stop();
		}
		else if (command.getName().equalsIgnoreCase("hunt"))
		{
			if (!(sender instanceof Player) || !Util.permission((Player) sender, "monsterhunt.usercmd.hunt", true)) return true; 
			
			MonsterHuntWorld world = worlds.get(((Player) sender).getWorld().getName());
			if (world == null || world.getWorld() == null) return true;
			if (world.Score.containsKey(((Player) sender).getName()))
					return true;
			if (world.state < 2)
			{
				String message = world.settings.getString("Messages.SignupBeforeHuntMessage");
	    		message = message.replace("<World>", world.name);
	    		Util.Message(message, sender);
				world.Score.put(((Player) sender).getName(), 0);
				
			}
			else if (world.state == 2 && world.getSignUpPeriodTime() == 0)
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
		}
		else if (command.getName().equalsIgnoreCase("huntscore"))
		{
			if (!(sender instanceof Player) || !Util.permission((Player) sender, "monsterhunt.usercmd.huntscore", true)) return true; 
			if (highscore.containsKey(((Player) sender).getName()))
			{
				Util.Message("Your high score is " + String.valueOf(highscore.get(((Player) sender).getName())) + " points.", sender);
				
			}
			else
			{
				Util.Message("You do not have your high score yet.", sender);
			}
		}
		else if (command.getName().equalsIgnoreCase("huntstatus"))
		{
			if (!(sender instanceof Player) || !Util.permission((Player) sender, "monsterhunt.usercmd.huntstatus", true)) return true; 
			
			Player player = (Player) sender;
			Boolean anyactive = false;
			String actives = "";
			for (MonsterHuntWorld world : worlds.values())
			{
				if (world.state > 0)
				{
					anyactive = true;
					actives += world.name + ",";
				}
			}
			if (!anyactive)
			{
				Util.Message(Settings.globals.getString("Messages.MessageHuntStatusNotActive"), player);
			}
			else
			{
				Util.Message(Settings.globals.getString("Messages.MessageHuntStatusHuntActive").replace("<Worlds>", actives.substring(0, actives.length()-1)) , player);
			}
			MonsterHuntWorld world = worlds.get(player.getWorld().getName());
			if (world == null || world.getWorld() == null) return true;
			
			if (world.state == 0)
			{
				
				if (world.lastScore.containsKey(player.getName()))
					Util.Message(world.settings.getString("Messages.MessageHuntStatusLastScore").replace("<Points>", String.valueOf(world.lastScore.get(player.getName()))),player);
				else
					Util.Message(world.settings.getString("Messages.MessageHuntStatusNotInvolvedLastHunt"),player);
			}
			else if (world.state == 2)
			{
				if (world.Score.containsKey(player.getName()))
				{
						if (world.Score.get(player.getName()) == 0 )
							Util.Message(world.settings.getString("Messages.MessageHuntStatusNoKills"), player);
						else
							Util.Message(world.settings.getString("Messages.MessageHuntStatusCurrentScore").replace("<Points>", String.valueOf(world.Score.get(player.getName()))), player);
				}
				if (world.settings.getBoolean("TellTime") && !world.manual)
				{
					int timediff = world.settings.getInt("EndTime")-world.settings.getInt("StartTime");
					long time = player.getWorld().getTime();
    				long curdiff = (time - world.settings.getInt("StartTime")) * 100;
    				double calc = curdiff/timediff;
    				int curpercent = (int) (100 - Math.round(calc));
    				curpercent +=  100;
    				curpercent /= 1;
    				Util.Message(world.settings.getString("Messages.MessageHuntStatusTimeReamining").replace("<Timeleft>", String.valueOf(curpercent)) , player);
				}
				
				
			}

		}
		else if (command.getName().equalsIgnoreCase("huntzone"))
		{
			if (!(sender instanceof Player) || !Util.permission((Player) sender, "monsterhunt.command.huntzone", true)) return true; 
			HuntZoneCreation.selectstart((Player) sender);
		}
    	return true;
    }
    	

}
