package listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import com.matejdro.bukkit.monsterhunt.MonsterHunt;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Util;

public class MonsterHuntEntityListener extends EntityListener {
private MonsterHunt plugin;
	HashMap<Integer, Player> lastHits = new HashMap<Integer, Player>();
	HashMap<Integer, Integer> lastHitCauses = new HashMap<Integer, Integer>();
	public MonsterHuntEntityListener(MonsterHunt instance)
	{
		plugin = instance;
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;

		if (!(event instanceof EntityDamageByEntityEvent)) 
		{
			if (event.getEntity() != null && event.getEntity() instanceof LivingEntity)
			{
				lastHits.remove(event.getEntity().getEntityId());
				lastHitCauses.remove(event.getEntity().getEntityId());
			}
			return;
		}
		
		EntityDamageByEntityEvent eventek = (EntityDamageByEntityEvent) event;
		if (eventek.getDamager() == null) 
			{
				if (event.getEntity() != null && event.getEntity() instanceof LivingEntity)
				{
					lastHits.remove(eventek.getEntity().getEntityId());
					lastHitCauses.remove(eventek.getEntity().getEntityId());
				}
				return;
			}
		MonsterHuntWorld world = plugin.worlds.get(eventek.getDamager().getWorld().getName());

		if (world == null || world.getWorld() == null) return;
		

		if (eventek.getEntity() instanceof LivingEntity && (eventek.getDamager() instanceof Player || eventek.getDamager() instanceof Wolf) && world.state == 2)
		{

			Player killer;
			int cause;
			if (eventek.getDamager() instanceof Wolf)
			{
				Wolf wolf = (Wolf) eventek.getDamager();
				if (!wolf.isTamed()) 
					{
						lastHits.remove(eventek.getEntity().getEntityId());
						lastHitCauses.remove(eventek.getEntity().getEntityId());
						return;
					}
				killer = (Player) wolf.getOwner();
				cause = -2;
			}
			else
			{
				killer = (Player) eventek.getDamager();
				ItemStack hand = killer.getItemInHand();
				if (hand == null)
					cause = 0;
				else
					cause = hand.getTypeId();
			}
			
			
			if (event instanceof EntityDamageByProjectileEvent) 
			{
				cause = -1;
			}
				lastHits.put(eventek.getEntity().getEntityId(), killer);	
				lastHitCauses.put(eventek.getEntity().getEntityId(), cause);
		}
		else if (event.getEntity() != null && event.getEntity() instanceof LivingEntity)
		{
			lastHits.remove(event.getEntity().getEntityId());
			lastHitCauses.remove(event.getEntity().getEntityId());
		}
    }
		
	public void onEntityDeath (EntityDeathEvent event) {
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			MonsterHuntWorld world = plugin.worlds.get(player.getWorld().getName());
			
			if (world == null || world.getWorld() == null) return;
			if (world.settings.getInt("DeathPenalty") == 0) return;
			
			if (world.state > 1 && world.Score.containsKey(player.getName()))
			{
				double score = world.Score.get(player.getName()) + 0.00;
				score = score - (score * world.settings.getInt("DeathPenalty") / 100.00);	
				world.Score.put(player.getName(), (int) Math.round(score));
				Util.Message(world.settings.getString("Messages.DeathMessage"),player);
			}
		}
		
		if (lastHits.containsKey(event.getEntity().getEntityId()))
			{

					if (event.getEntity() == null) return;
					
					Player killer = lastHits.get(event.getEntity().getEntityId());
					MonsterHuntWorld world = plugin.worlds.get(event.getEntity().getWorld().getName());
					if (world == null || world.getWorld() == null) return;
					
					if (killer != null) kill(killer, (LivingEntity) event.getEntity(), world);
					
			}
	}
	
	private void kill(Player player, LivingEntity monster, MonsterHuntWorld world)
	{
			String name;
			
			String cause = "General";
			int causenum = lastHitCauses.get(monster.getEntityId());
			if (causenum == -1)
				cause = "Arrow";
			else if (causenum == -2)
				cause = "Wolf";
			else 
				cause = String.valueOf(causenum);
			
			int points = 0;
			if (monster instanceof Skeleton)
			{
				points = world.settings.getMonsterValue("Skeleton", cause);
				name = "Skeleton";
			}
			else if (monster instanceof Spider)
			{
				points = world.settings.getMonsterValue("Spider", cause);
				name = "Spider";
			}
			else if (monster instanceof Creeper)
			{
				Creeper creeper = (Creeper) monster;
				if (creeper.isPowered())
				{
					points = world.settings.getMonsterValue("ElectrifiedCreeper", cause);
					name = "Electrified Creeper";
				}
				else
				{
					points = world.settings.getMonsterValue("Creeper", cause);
					name = "Creeper";
				}
			}
			else if (monster instanceof Ghast)
			{
				points = world.settings.getMonsterValue("Ghast", cause);
				name = "Ghast";
			}
			else if (monster instanceof Slime)
			{
				points = world.settings.getMonsterValue("Slime", cause);
				name = "Slime";
			}
			else if (monster instanceof PigZombie)
			{
				points = world.settings.getMonsterValue("ZombiePigman", cause);
				name = "Zombie Pigman";
			}
			else if (monster instanceof Giant)
			{
				points = world.settings.getMonsterValue("Giant", cause);
				name = "Giant";
			}
			else if (monster instanceof Zombie)
			{
				points = world.settings.getMonsterValue("Zombie", cause);
				name = "Zombie";
			}
			else if (monster instanceof Wolf)
			{
				Wolf wolf = (Wolf) monster;
				if (wolf.isTamed())
				{
					points = world.settings.getMonsterValue("TamedWolf", cause);
					name = "Tamed Wolf";
				}
				else
				{
					points = world.settings.getMonsterValue("WildWolf", cause);
					name = "Wild Wolf";
				}
				
			}
			else if (monster instanceof Player)
			{
				points = world.settings.getMonsterValue("Player", cause);
				name = "Player";
			}
			else
			{
				return;
			}
			if (points < 1) return;
			
			if (!world.Score.containsKey(player.getName()) && !world.settings.getBoolean("EnableSignup"))
				world.Score.put(player.getName(), 0);
			if (world.Score.containsKey(player.getName()))
			{
				if (!world.properlyspawned.contains(monster.getEntityId()) && world.settings.getBoolean("OnlyCountMobsSpawnedOutside"))
				{
					String message = world.settings.getString("Messages.KillMobSpawnedInsideMessage");
					Util.Message(message, player);
					world.blacklist.add(monster.getEntityId());
					return;
					
				}
				int newscore = world.Score.get(player.getName()) + points;

				if (world.settings.getBoolean("AnnounceLead"))
				{
					Entry<String, Integer> leadpoints = null;
					for (Entry<String,Integer> e : world.Score.entrySet())
					{
						if (leadpoints == null || e.getValue() > leadpoints.getValue() || (e.getValue() == leadpoints.getValue() && leadpoints.getKey().equalsIgnoreCase(player.getName())))
						{
							leadpoints = e;
						}
							
					}
					Util.Debug(leadpoints.toString());
					Util.Debug(String.valueOf(newscore));
					Util.Debug(String.valueOf(!leadpoints.getKey().equals(player.getName())));
										
					if (leadpoints != null && newscore > leadpoints.getValue() && !leadpoints.getKey().equals(player.getName()))
					{
						String message = world.settings.getString("Messages.MessageLead");
						message = message.replace("<Player>", player.getName());
						message = message.replace("<Points>", String.valueOf(newscore));
						message = message.replace("<World>", world.name);
						Util.Broadcast(message);
						
					}
						
				}
				
				world.Score.put(player.getName(), newscore);
				world.blacklist.add(monster.getEntityId());
				
				world.properlyspawned.remove((Object) monster.getEntityId());
				
				String message = world.settings.getKillMessage(cause);
				message = message.replace("<MobValue>", String.valueOf(points));
				message = message.replace("<MobName>", name);
				message = message.replace("<Points>",String.valueOf(newscore));
				Util.Message(message,player);
				
				lastHits.remove(monster.getEntityId());
				lastHitCauses.remove(monster.getEntityId());
			}
	}
	
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Creature)
		{
			MonsterHuntWorld world = plugin.worlds.get(event.getLocation().getWorld().getName());
			if (world == null || world.getWorld() == null) return;
			if (world.state == 0) return;
			if (!world.settings.getBoolean("OnlyCountMobsSpawnedOutside")) return;
			Block block = event.getLocation().getBlock();
			int number = 0;
			while (block.getY() < 125)
			{
				number++;
				block = block.getFace(BlockFace.UP);
				Boolean empty = false;
				
				if (block.getType() == Material.AIR || block.getType() == Material.LEAVES) empty = true;
				else if (block.getType() == Material.LOG)
				{
					if (block.getFace(BlockFace.NORTH).getType() == Material.AIR || block.getFace(BlockFace.NORTH).getType() == Material.LEAVES) empty = true;
					else if (block.getFace(BlockFace.EAST).getType() == Material.AIR || block.getFace(BlockFace.EAST).getType() == Material.LEAVES) empty = true;
					else if (block.getFace(BlockFace.WEST).getType() == Material.AIR || block.getFace(BlockFace.WEST).getType() == Material.LEAVES) empty = true;
					else if (block.getFace(BlockFace.SOUTH).getType() == Material.AIR || block.getFace(BlockFace.SOUTH).getType() == Material.LEAVES) empty = true;
					else if (block.getFace(BlockFace.UP).getType() == Material.AIR || block.getFace(BlockFace.UP).getType() == Material.LEAVES) empty = true;
					else if (block.getFace(BlockFace.DOWN).getType() == Material.AIR || block.getFace(BlockFace.DOWN).getType() == Material.LEAVES) empty = true;
				}
				
				if (!empty) return;
				if (world.settings.getInt("OnlyCountMobsSpawnedOutsideHeightLimit") > 0 && world.settings.getInt("OnlyCountMobsSpawnedOutsideHeightLimit") < number) break;
			}
				world.properlyspawned.add(event.getEntity().getEntityId());

			
		}

	}
	
}
