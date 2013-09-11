package TCB.TabDeco;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerKillDeathListener implements Listener
{
	private TabDeco plugin;
	
	public PlayerKillDeathListener(TabDeco plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler()
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		try
		{
			Player player = (Player)event.getEntity();
			if(player != null)
			{
				FixedMetadataValue metadata = (FixedMetadataValue)TabDeco.getMetadata(player, "deathCounter", plugin);
				TabDeco.setMetadata(player, "deathCounter", new FixedMetadataValue(plugin, metadata != null ? metadata.asInt() + 1 : 1), plugin);
				metadata = (FixedMetadataValue)TabDeco.getMetadata(player, "deathCounter", plugin);
				
				if(plugin.getData() != null) {
					plugin.setData("player." + player.getName() + ".deaths", (int)(plugin.getData().getInt("player." + player.getName() + ".deaths")) + 1);
				} else {
					plugin.setData("player." + player.getName() + ".deaths", 1);
				}
				
				if(TabDeco.debugMode)TabDeco.debugLogger.info("Local DeathCounter of " + player.getDisplayName() + ": " + metadata.asInt());
				if(TabDeco.debugMode)TabDeco.debugLogger.info("Global DeathCounter of " + player.getDisplayName() + ": " + (int)(plugin.getData().getInt("player." + player.getName() + ".deaths")));
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	@EventHandler()
	public void onPlayerKilled(PlayerDeathEvent event)
	{
		try
		{
			Player player = (Player)event.getEntity();
			if(player != null)
			{
				player = player.getKiller();
				if(player != null)
				{
					FixedMetadataValue metadata = (FixedMetadataValue)TabDeco.getMetadata(player, "killCounter", plugin);
					TabDeco.setMetadata(player, "killCounter", new FixedMetadataValue(plugin, metadata != null ? metadata.asInt() + 1 : 1), plugin);
					if(plugin.getData() != null) {
						plugin.setData("player." + player.getName() + ".kills", (int)(plugin.getData().getInt("player." + player.getName() + ".kills")) + 1);
					} else {
						plugin.setData("player." + player.getName() + ".kills", 1);
					}
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Local KillCounter of " + player.getDisplayName() + ": " + metadata.asInt());
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Global KillCounter of " + player.getDisplayName() + ": " + (int)(plugin.getData().getInt("player." + player.getName() + ".kills")));
				}
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	@EventHandler()
    public void onEntityDeath(EntityDeathEvent e) {
        Player player = (Player) e.getEntity().getKiller();
        if(player != null && e.getEntity() instanceof Player == false) {
        	FixedMetadataValue metadata = (FixedMetadataValue)TabDeco.getMetadata(player, "mobkills", plugin);
			TabDeco.setMetadata(player, "mobkills", new FixedMetadataValue(plugin, metadata != null ? metadata.asInt() + 1 : 1), plugin);
			if(plugin.getData() != null) {
				plugin.setData("player." + player.getName() + ".mobkills", (int)(plugin.getData().getInt("player." + player.getName() + ".mobkills")) + 1);
			} else {
				plugin.setData("player." + player.getName() + ".mobkills", 1);
			}
			if(TabDeco.debugMode)TabDeco.debugLogger.info("Local MobKills of " + player.getDisplayName() + ": " + metadata.asInt());
			if(TabDeco.debugMode)TabDeco.debugLogger.info("Global MobKills of " + player.getDisplayName() + ": " + (int)(plugin.getData().getInt("player." + player.getName() + ".mobkills")));
        }
    }
}
