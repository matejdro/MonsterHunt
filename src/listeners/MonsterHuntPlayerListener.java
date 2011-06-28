package listeners;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.matejdro.bukkit.monsterhunt.HuntZoneCreation;
import com.matejdro.bukkit.monsterhunt.Settings;

public class MonsterHuntPlayerListener extends PlayerListener {
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == Settings.globals.getInt("SelectionTool", 268))
		{
			if ( HuntZoneCreation.players.containsKey(event.getPlayer().getName()))
			{
				HuntZoneCreation.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
		}

	}
		
}
