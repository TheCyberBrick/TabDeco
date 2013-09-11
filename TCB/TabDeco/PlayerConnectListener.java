package TCB.TabDeco;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerConnectListener implements Listener
{
	private TabDeco plugin;
	
	public PlayerConnectListener(TabDeco plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		/**Resetting local variables**/
		TabDeco.setMetadata(player, "killCounter", new FixedMetadataValue(plugin, 0), plugin);
		TabDeco.setMetadata(player, "mobkills", new FixedMetadataValue(plugin, 0), plugin);
		TabDeco.setMetadata(player, "deathCounter", new FixedMetadataValue(plugin, 0), plugin);
		
		plugin.displayAllPlayerForPlayer(player, false, true);
		if(TabDeco.debugMode)TabDeco.debugLogger.info("Player " + player.getDisplayName() + " is logging in! Sending packets.");
		ArrayList<String> lastTabData = new ArrayList<String>();
		for(int i = 0; i < this.plugin.tabData.size(); i++)
		{
			String text = this.plugin.tabData.get(i).toString();
			text = this.plugin.replaceAllWords(text, player, true);
			plugin.sendSpecificPacketToPlayer(player, true, text.replaceAll("\\[\\@\\]", ""));
			lastTabData.add(text);
			if(TabDeco.debugMode)TabDeco.debugLogger.info("Sent packet with text: " + text);
		}
		plugin.setPlayerData(player, "playerTabData", lastTabData);
		plugin.displayAllPlayerForPlayer(player, true, true);
	}
}
