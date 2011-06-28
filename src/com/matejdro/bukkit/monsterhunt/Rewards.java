package com.matejdro.bukkit.monsterhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

public class Rewards {

	public MonsterHunt plugin;
	
	public Rewards(MonsterHunt instance)
	{
		plugin = instance;
	}
	
	public void Reward(MonsterHuntWorld world)
	{
		NormalReward(world);
	}
	
	private void NormalReward(MonsterHuntWorld world)
	{
		
		HashMap<String,Integer>[] Winners = GetWinners(world);
		if (Winners[0].size() < 1)
		{
			String message = world.settings.getString("Messages.FinishMessageNotEnoughPoints");
			message = message.replace("<World>", world.name);
			plugin.Broadcast(message);
			return;
		}
		int num = world.settings.getInt("Rewards.NumberOfWinners");
		
		int score = Winners[0].get(Winners[0].keySet().toArray()[0]);
		if (score < world.settings.getInt("MinimumPointsPlace1"))
		{
			String message = world.settings.getString("Messages.FinishMessageNotEnoughPoints");
			message = message.replace("<World>", world.name);
			plugin.Broadcast(message);
			return;
			
		}
		String RewardString;
		
		//Normal reward
			if (world.settings.getBoolean("Rewards.EnableReward"))
			{
				for (int place = 0; place<num; place++)
				{
					if (Winners[place].size() < 1) continue;
					score = Winners[place].get(Winners[place].keySet().toArray()[0]);
					plugin.Debug(String.valueOf(score));
					plugin.Debug(String.valueOf(world.settings.getInt("MinimumPointsPlace" + String.valueOf(place + 1))));
					if (score < world.settings.getInt("MinimumPointsPlace" + String.valueOf(place + 1))) Winners[place].clear();
					for (String i : Winners[place].keySet())
					{
						RewardString = world.settings.getString("Rewards.RewardParametersPlace" + String.valueOf(place + 1));
						if (RewardString.contains(";"))
							RewardString = PickRandom(RewardString);
						Reward(i, RewardString, world, score);
					}
			}
		}
		
		//RewardEveryone
		if (!(!world.settings.getBoolean("Rewards.EnableRewardEveryonePermission") && !world.settings.getBoolean("Rewards.RewardEveryone")))
		{
			for (Entry i : world.Score.entrySet())
			{
				Player player = plugin.getServer().getPlayer((String) i.getKey());
				if (player == null) continue;
				RewardString = world.settings.getString("Rewards.RewardParametersEveryone");
				if (RewardString.contains(";"))
					RewardString = PickRandom(RewardString);
				if (world.settings.getBoolean("Rewards.RewardEveryone") || (plugin.permission(player, "monsterhunt.rewardeverytime", false) && world.settings.getBoolean("Rewards.EnableRewardEveryonePermission")))
				{
					Reward((String) i.getKey(), RewardString, world, (Integer) i.getValue());
				}
			}
		}
		
		//Broadcast winner message
		MonsterHunt.Debug("[MonterHunt][DEBUG - NEVEREND]Broadcasting Winners");
		String message;
		
			message = world.settings.getString("Messages.FinishMessageWinners");
			message = message.replace("<World>", world.name);

			for (int place = 0; place<num; place++)
			{
				String players = "";
				if (Winners[place].size() < 1)
				{			
					players = "Nobody";
					score = 0;
				}
				else
				{
					score = Winners[place].get(Winners[place].keySet().toArray()[0]);
					for (String i : Winners[place].keySet())
					{
						players += i + ", ";
					}
					players = players.substring(0, players.length() - 2);
				}
				
				message = message.replace("<NamesPlace" + String.valueOf(place + 1) + ">", players);
				message = message.replace("<PointsPlace" + String.valueOf(place + 1) + ">", String.valueOf(score));

				
			}
		plugin.Broadcast(message);
	}
	
	private void Reward(String playerstring, String RewardString, MonsterHuntWorld world, int score)
	{
		String[] split = RewardString.split(",");
		Player player = plugin.getServer().getPlayer(playerstring);
		if (player == null) return;
		String items = "";
		for (String i2 : split)
		{
			plugin.Debug(i2);
			//Parse block ID
			String BlockIdString = i2.substring(0,i2.indexOf(" "));
			short data;
			int BlockId;
			if (BlockIdString.contains(":"))
			{
				BlockId = Integer.valueOf(BlockIdString.substring(0, i2.indexOf(":")));
				data = Short.valueOf(BlockIdString.substring(i2.indexOf(":") + 1));
			}
			else
			{
				BlockId = Integer.valueOf(BlockIdString);
				data = 0;
			}
			
			
			//Parse block amount
			String rv = i2.substring(i2.indexOf(" ")+1);
			Boolean RelativeReward = false;
			if (rv.startsWith("R"))
			{
				RelativeReward = true;
				rv = rv.substring(1);
			}
			int StartValue, EndValue;
			if (rv.contains("-"))
			{
				StartValue = (int) Math.round(Double.valueOf(rv.substring(0,rv.indexOf("-"))) * 100.0);
				EndValue = (int) Math.round(Double.valueOf(rv.substring(rv.indexOf("-")+1)) * 100.0);
			}
			else
			{
				StartValue = (int) Math.round(Double.valueOf(rv) * 100.0);
				EndValue = StartValue;

			}
			int random;
			if (EndValue == StartValue)
				random = EndValue;
			else
			{
				random = new Random().nextInt(EndValue - StartValue) + StartValue;		
			}
			double number = random / 100.0;
			if (RelativeReward)
				number *= score;
			 int amount = (int)Math.round(number);
			 
			//give reward
			if (BlockId == 0)
			{
				iConomyReward(playerstring,amount);
				if (amount > 0) items += String.valueOf(amount) + "x " + world.settings.getString("Messages.iConomyCurrencyName") + ", "; 

			}
			else
			{
				addItemFix(player, BlockId, amount, data);
				if (amount > 0) items += String.valueOf(amount) + "x " + getMaterialName(Material.getMaterial(BlockId)) + ", "; 
				//plugin.getServer().getPlayer(i).giveItem(BlockId,amount);
			}
		}
		if (items.trim() == "") return;
		String message= world.settings.getString("Messages.RewardMessage");
		items = items.substring(0, items.length() - 2);
		message =message.replace("<Items>", items);
		plugin.Message(message, player);

		
	}
	private void iConomyReward(String player, int number)
	{		
		Plugin test = plugin.getServer().getPluginManager().getPlugin("iConomy");
		if(test != null) {
			iConomy iConomy = (iConomy) test;
			Account account = iConomy.getAccount(player);
			if (account == null) return;
			Holdings balance = account.getHoldings();
			balance.add(number);
		} else {
			MonsterHunt.log.log(Level.WARNING, "[MonsterHunt]: You have iConomy rewards enabled, but don't hav iConomy installed! Some players may not get their reward!");
		}
	}
	private String PickRandom(String RewardString )
	{
		String[] split = RewardString.split(";");
		int[] chances = new int[split.length];
		
		int totalchances = 0,  numnochances = 0;
		for (int i = 0;i< split.length; i++)
		{
			if (split[i].startsWith(":"))
			{
				chances[i] = Integer.valueOf(split[i].substring(1, split[i].indexOf(" ")));
				split[i] = split[i].substring(split[i].indexOf(" ") + 1);
				totalchances+=chances[i];
			}
			else
			{
				chances[i] = -1;
				numnochances++;
			}
				
		}
		
		if (totalchances > (100 - numnochances))
		{
			plugin.log.warning("[MonsterHunt]Invalid Rewards configuration! MonsterHunt will now throw error and disable itself.");
			plugin.getPluginLoader().disablePlugin(plugin);
			return null;
		}
		
		if (numnochances > 0)
		{
			int averagechance = (100 - totalchances) / numnochances;
			for (int i=0; i< chances.length; i++)
			{
				chances[i] = averagechance;
			}
		}
		
		int total = 0;
		
		for (int i=0; i< split.length;i++)
		{
			total+= chances[i];
			chances[i] = total;
		}
		
		int random = new Random().nextInt(100);
		for (int i=0; i< split.length;i++)
		{
			if (random < chances[i] && (i < 1 || random >= chances[i-1])) return split[i];
		}
		return "";
		
	}
	private HashMap<String,Integer>[] GetWinners(MonsterHuntWorld world)
	{
		HashMap<String, Integer> scores = new HashMap<String, Integer>();
		scores.putAll(world.Score);
		int num = world.settings.getInt("Rewards.NumberOfWinners");
		HashMap<String,Integer>[] winners = new HashMap[num];
		for (int place = 0; place<num; place++)
		{
			winners[place] = new HashMap<String, Integer>();
			int tmp = 0;
			for (String i : scores.keySet())
			{
				int value = scores.get(i);
				if (value > tmp)
				{
					winners[place].clear();
					winners[place].put(i, value);
					tmp = value;
				}
				else if (value == tmp)
				{
					winners[place].put(i, value);
				}
			}
			
			for (String i : winners[place].keySet())
			{
				scores.remove(i);
			}
		}
		
		return winners;
	}
	
	// Material name snippet by TechGuard
	public String getMaterialName(Material material){
        String name = material.toString();
        name = name.replaceAll("_", " ");
        if(name.contains(" ")){
            String[] split = name.split(" ");
            for(int i=0; i < split.length; i++){
                split[i] = split[i].substring(0, 1).toUpperCase()+split[i].substring(1).toLowerCase();
            }
            name = "";
            for(String s : split){
                name += " "+s;
            }
            name = name.substring(1);
        } else {
            name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
        }
        return name;
}
	//add item color by fabe
	public void addItemFix(Player players, int ID, int amount, short dur) {
		if (players.getInventory().firstEmpty() == -1)
		{
			players.getLocation().getWorld().dropItem(players.getLocation(), new ItemStack(ID, amount, dur));
			return;
		}
        if (players.getInventory().contains(ID) && (ID == 35 || ID == 351)) { // Wool or Dye
            HashMap<Integer, ? extends ItemStack> invItems = players.getInventory()
                    .all(ID);

            int restAmount = amount;
            for (Map.Entry<Integer, ? extends ItemStack> entry : invItems.entrySet()) {

                int index = entry.getKey();
                ItemStack item = entry.getValue();
                int stackAmount = item.getAmount();

                // e.g. same wool in inventory => put in to stack
                if (dur == item.getDurability()) {

                    if (stackAmount < 64) {
                        // Add to stack
                        int canGiveAmount = 64 - stackAmount;
                        int giveAmount;

                        if (canGiveAmount >= restAmount) {
                            giveAmount = restAmount;
                            restAmount = 0;
                        } else {
                            giveAmount = canGiveAmount;
                            restAmount = restAmount - giveAmount;
                        }

                        players.getInventory()
                                .setItem(
                                        index,
                                        new ItemStack(ID, stackAmount
                                                + giveAmount, dur));

                    }
                }
            }
            // If there is still a rest, add the rest to the inventory
            if (restAmount > 0) {
                int emptySlot = players.getInventory().firstEmpty();
                players.getInventory().setItem(emptySlot,
                        new ItemStack(ID, restAmount, dur));
            }
        } else {
            // Standard usage of addItem
            players.getInventory().addItem(new ItemStack(ID, amount, dur));
        }
    }
	
}
