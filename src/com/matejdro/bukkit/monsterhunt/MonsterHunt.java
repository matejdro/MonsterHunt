package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

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
	private InputOutput io = new InputOutput(this);
	private MonsterHuntEntity EntityListener;
	private Rewards Reward;
	Timer timer;
	
	public HashMap<String,Integer> highscore = new HashMap<String,Integer>();
	public HashMap<String,MonsterHuntWorld> worlds = new HashMap<String,MonsterHuntWorld>();
	
	private static Plugin permissions = null;
	private Plugin extendday = null;
	
	public static MonsterHunt instance;	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		for (MonsterHuntWorld world : worlds.values())
			stop(world);
	}

	@Override
	public void onEnable() {
	initialize();	
		
	io.LoadSettings();
	io.PrepareDB();
	if (Settings.globals.getBoolean("EnableHighScores", false)) io.LoadHighScores();
	
	
	
		
	getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, EntityListener, Event.Priority.Monitor, this);
	getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, EntityListener, Event.Priority.Monitor, this);
	getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, EntityListener, Event.Priority.Monitor, this);
	
	log.log(Level.INFO, "[MonsterHunt] MonsterHunt Loaded!");

	permissions = this.getServer().getPluginManager().getPlugin("Permissions");
	extendday = this.getServer().getPluginManager().getPlugin("ExtendDay");
	
	timer();

	}
	
	private void initialize()
	{
		EntityListener = new MonsterHuntEntity(this);
		Reward = new Rewards(this);
		instance = this;
		
	}
	
	public void Message(String message, CommandSender sender)
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
	
	public void Message(String message, Player player)
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
	public void Broadcast(String message)
	{
		for (Player i : getServer().getOnlinePlayers())
		{
			Message(message,i);
		}
			
	}
	
	public static void Debug(String message)
	{
		if (Settings.globals.getBoolean("Debug", false))
			log.info("[MonsterHunt][Debug]" + message);
	}
	
	public void start(MonsterHuntWorld world)
	{
		String message = world.settings.getString("Messages.StartMessage");
		message = message.replace("<World>", world.name);
		Broadcast(message);
		world.state = 2;
		world.waitday = true;
	}
	public void stop(MonsterHuntWorld world)
	{
		if (world.state < 2) return;
		if (world.Score.size() < world.settings.getInt("MinimumPlayers"))
		{
			String message = world.settings.getString("Messages.FinishMessageNotEnoughPlayers");
			message = message.replace("<World>", world.name);
			Broadcast(message);
		}
		else
		{
			Reward.Reward(world);
		}
		world.state = 0;
		if (Settings.globals.getBoolean("EnableHighScores", false))
		{
			for (String i : world.Score.keySet())
			{
				int hs = highscore.containsKey(i) ? highscore.get(i) : 0;
				int score = world.Score.get(i);
				if (score > hs)
				{
					highscore.put(i,score);
					io.UpdateHighScore(i, score);
					Player player = getServer().getPlayer(i);
					if (player != null) 
						{
						String message = world.settings.getString("Messages.HighScoreMessage");
						message = message.replace("<Points>", String.valueOf(score));
						Message(message, player);
						}
				}
			}
		}
		
		world.lastScore.putAll(world.Score);
		world.Score.clear();
		world.blacklist.clear();
		world.properlyspawned.clear();
	}

	private void timer()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

		    public void run() {
		    	for (MonsterHuntWorld world : worlds.values())
		    	  {
		    		  if (world == null || world.getWorld() == null) return;
		    		  long time = world.getWorld().getTime();
		    	      
		    	      if (world.state == 0 && time < world.settings.getInt("EndTime") && time > world.getSignUpPeriodTime() && world.getSignUpPeriodTime() > 0 && !world.manual && !world.waitday && isNight(world.getWorld()))
		    	      {
		    	    	  if (WillBe(world))
		    	    	  {
		    	    		  world.state = 1;
		    	    		  String message = world.settings.getString("Messages.MessageSignUpPeriod");
		    	    		  message = message.replace("<World>", world.name);
		    	        	  Broadcast(message);
		    	    		  
		    	    	  }
		    	    	  world.waitday = true;
		    	    	  
		    	      }
		    	      else if (world.state < 2 && time > world.settings.getInt("StartTime") && time < world.settings.getInt("EndTime") && !world.manual)
		    		  {
		    	    	  if (world.state == 1)
		    	    	  {
		    	    		  if (world.Score.size() < world.settings.getInt("MinimumPlayers") && world.settings.getBoolean("EnableSignup"))
		    	    		  {
		        	    		Broadcast(world.settings.getString("Messages.MessageStartNotEnoughPlayers"));
		    	    			world.state = 0;
		    	    			world.Score.clear();
		    	    			world.waitday=true;
		    	    			StartFailed(world);
		    	    		  }
		    	    				
		    	    			else
		    	    			{
		    	    				start(world);
		    	    			}
		    	    				
		    	    			
		    	    	  }
		    	    	  else if (!world.waitday && world.settings.getInt("SignUpPeriodTime") == 0 && isNight(world.getWorld()) )
		    	    	  {
		    	    		  world.waitday = true;
		    	    		  if (WillBe(world)) start(world);
			    				
		    	    	  }
		    	      }
		    	      else if(world.state == 2 && (time > world.settings.getInt("EndTime") || time < world.settings.getInt("StartTime")) && !world.manual && isDay(world.getWorld()))
		    	      {
		    	    	  MonsterHunt.Debug("[MonterHunt][DEBUG - NEVEREND]Stop Time");
		    	    	 stop(world);
		    	      }
		    	      else if(world.waitday && (time > world.settings.getInt("EndTime") || time < world.settings.getInt("StartTime") - world.getSignUpPeriodTime()) && isDay(world.getWorld()))
		    	      {
		    	    	  world.waitday = false;
		    	      }
		    	  }
		    }
		}, 200L, 40L);
	}
	
    public Boolean WillBe(MonsterHuntWorld world)
    {
    	Debug("WillBe");
    	if (world.curday == 0)
  	  {
  		  
  			
  		world.curday = world.settings.getInt("SkipDays");
  		if ((new Random().nextInt(100)) < world.settings.getInt("StartChance"))
		  {
			return true;
		  }
  		
  	  } else {
  		world.curday--;  
  	  }
    	return false;
    }
	
    public static Boolean permission(Player player, String line, Boolean def)
    {
    	    if(permissions != null) {
    	    	return (((Permissions) permissions).getHandler()).has(player, line);
    	    } else {
    	    	return def;
    	    }
    }
    
    public Boolean isDay(World world)
    {
    	MonsterHunt.Debug("extendday: " + String.valueOf(extendday));
    	    if(extendday != null) {
    	    	int counter = ((ExtendedDay) extendday).getNightsLeftCounter((int) world.getId());
    	    	MonsterHunt.Debug("ExtendDay nights left: " + String.valueOf(counter));
    	    	return counter > 0;
    	    } else {
    	    	return true;
    	    }
    }
    
    public Boolean isNight(World world)
    {
    	MonsterHunt.Debug("extendday: " + String.valueOf(extendday));
    	if(extendday != null) {
	    	int counter = ((ExtendedDay) extendday).getDaysLeftCounter((int) world.getId());
	    	MonsterHunt.Debug("ExtendDay days left: " + String.valueOf(counter));
	    	return counter > 0;
	    } else {
	    	return true;
	    }
    }
    
    public void StartFailed(MonsterHuntWorld world)
    {
    	if (world.settings.getInt("SkipToIfFailsToStart") >= 0)
    	{
    		world.getWorld().setTime(world.settings.getInt("SkipToIfFailsToStart"));
    	}
    }
    
    public int RemainingNights(World world)
    {
    	if(extendday != null) {
	    	
	    	return ((ExtendedDay) extendday).getNightsLeftCounter((int) world.getId());
	    } else {
	    	return 0;
	    }
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equalsIgnoreCase("huntstart")  )
		{
			if (sender instanceof Player && !permission((Player) sender, "monsterhunt.admincmd.huntstart", true)) return true; 
			
			if (args.length < 1)
			{
				Message("Usage: /huntstart [World Name]",sender);
				return true;
			}
			else if (!worlds.containsKey(args[0]))
			{
				Message("There is no such world!", sender);
				return true;
			}
			start(worlds.get(args[0]));
		}

		else if (command.getName().equalsIgnoreCase("huntstop"))
		{
			if (sender instanceof Player && !permission((Player) sender, "monsterhunt.admincmd.huntstop", true)) return true; 
			
			if (args.length < 1)
			{
				Message("Usage: /huntstop [World Name]",sender);
				return true;
			}
			else if (!worlds.containsKey(args[0]))
			{
				Message("There is no such world!", sender);
				return true;
			}
			stop(worlds.get(args[0]));
		}
		else if (command.getName().equalsIgnoreCase("hunt"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "monsterhunt.usercmd.hunt", true)) return true; 
			
			MonsterHuntWorld world = worlds.get(((Player) sender).getWorld().getName());
			if (world == null || world.getWorld() == null) return true;
			if (world.Score.containsKey(((Player) sender).getName()))
					return true;
			if (world.state < 2)
			{
				String message = world.settings.getString("Messages.SignupBeforeHuntMessage");
	    		message = message.replace("<World>", world.name);
				Message(message, sender);
				world.Score.put(((Player) sender).getName(), 0);
				
			}
			else if (world.state == 2 && world.getSignUpPeriodTime() == 0)
			{
				String message = world.settings.getString("Messages.SignupAtHuntMessage");
	    		message = message.replace("<World>", world.name);
				Message(message, sender);
				world.Score.put(((Player) sender).getName(), 0);
			}
			else
			{
				Message(world.settings.getString("Messages.MessageTooLateSignUp"), sender);
			}
		}
		else if (command.getName().equalsIgnoreCase("huntscore"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "monsterhunt.usercmd.huntscore", true)) return true; 
			if (highscore.containsKey(((Player) sender).getName()))
			{
				Message("Your high score is " + String.valueOf(highscore.get(((Player) sender).getName())) + " points.", sender);
				
			}
			else
			{
				Message("You do not have your high score yet.", sender);
			}
		}
		else if (command.getName().equalsIgnoreCase("huntstatus"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "monsterhunt.usercmd.huntstatus", true)) return true; 
			
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
				Message(Settings.globals.getString("Messages.MessageHuntStatusNotActive"), player);
			}
			else
			{
				Message(Settings.globals.getString("Messages.MessageHuntStatusHuntActive").replace("<Worlds>", actives.substring(0, actives.length()-1)) , player);
			}
			MonsterHuntWorld world = worlds.get(player.getWorld().getName());
			if (world == null || world.getWorld() == null) return true;
			
			if (world.state == 0)
			{
				
				if (world.lastScore.containsKey(player.getName()))
					Message(world.settings.getString("Messages.MessageHuntStatusLastScore").replace("<Points>", String.valueOf(world.lastScore.get(player.getName()))),player);
				else
					Message(world.settings.getString("Messages.MessageHuntStatusNotInvolvedLastHunt"),player);
			}
			else if (world.state == 2)
			{
				if (world.Score.containsKey(player.getName()))
				{
						if (world.Score.get(player.getName()) == 0 )
							Message(world.settings.getString("Messages.MessageHuntStatusNoKills"), player);
						else
							Message(world.settings.getString("Messages.MessageHuntStatusCurrentScore").replace("<Points>", String.valueOf(world.Score.get(player.getName()))), player);
				}
				if (world.settings.getBoolean("TellTime") && !world.manual)
				{
					int timediff = world.settings.getInt("EndTime")-world.settings.getInt("StartTime");
					long time = player.getWorld().getTime();
    				long curdiff = (time - world.settings.getInt("StartTime")) * 100;
    				double calc = curdiff/timediff;
    				int curpercent = (int) (100 - Math.round(calc));
    				int remnight = RemainingNights(world.getWorld());
    				curpercent +=  100 * remnight;
    				curpercent /= 1 + remnight;
    				Message(world.settings.getString("Messages.MessageHuntStatusTimeReamining").replace("<Timeleft>", String.valueOf(curpercent)) , player);
				}
				
				
			}

		}
    	return true;
    }
    	

}
