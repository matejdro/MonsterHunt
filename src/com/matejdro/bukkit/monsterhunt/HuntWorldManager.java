package com.matejdro.bukkit.monsterhunt;


public class HuntWorldManager {
	

	public static void timer()
	{
		MonsterHunt.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MonsterHunt.instance, new Runnable() {

		    public void run() {
		    	for (MonsterHuntWorld world : MonsterHunt.worlds.values())
		    	  {
		    		  if (world == null || world.getWorld() == null) return;
		    		  long time = world.getWorld().getTime();
		    	      
		    	      if (world.state == 0 && time < world.settings.getInt("EndTime") && time > world.getSignUpPeriodTime() && world.getSignUpPeriodTime() > 0 && !world.manual && !world.waitday)
		    	      {
		    	    	  if (world.canStart())
		    	    	  {
		    	    		  world.state = 1;
		    	    		  String message = world.settings.getString("Messages.MessageSignUpPeriod");
		    	    		  message = message.replace("<World>", world.name);
		    	    		  Util.Broadcast(message);
		    	    		  
		    	    	  }
		    	    	  world.waitday = true;
		    	    	  
		    	      }
		    	      else if (world.state < 2 && time > world.settings.getInt("StartTime") && time < world.settings.getInt("EndTime") && !world.manual)
		    		  {
		    	    	  if (world.state == 1)
		    	    	  {
		    	    		  if (world.Score.size() < world.settings.getInt("MinimumPlayers") && world.settings.getBoolean("EnableSignup"))
		    	    		  {
		    	    			  Util.Broadcast(world.settings.getString("Messages.MessageStartNotEnoughPlayers"));
		    	    			world.state = 0;
		    	    			world.Score.clear();
		    	    			world.waitday=true;
		    	    			world.skipNight();
		    	    		  }
		    	    				
		    	    			else
		    	    			{
		    	    				world.start();
		    	    			}
		    	    				
		    	    			
		    	    	  }
		    	    	  else if (!world.waitday && world.settings.getInt("SignUpPeriodTime") == 0)
		    	    	  {
		    	    		  world.waitday = true;
		    	    		  if (world.canStart()) world.start();
			    				
		    	    	  }
		    	      }
		    	      else if(world.state == 2 && (time > world.settings.getInt("EndTime") || time < world.settings.getInt("StartTime")) && !world.manual)
		    	      {
		    	    	  Util.Debug("[MonterHunt][DEBUG - NEVEREND]Stop Time");
		    	    	 world.start();
		    	      }
		    	      else if(world.waitday && (time > world.settings.getInt("EndTime") || time < world.settings.getInt("StartTime") - world.getSignUpPeriodTime()) )
		    	      {
		    	    	  world.waitday = false;
		    	      }
		    	  }
		    }
		}, 200L, 40L);
	}
}
