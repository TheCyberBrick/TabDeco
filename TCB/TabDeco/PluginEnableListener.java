package TCB.TabDeco;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

public class PluginEnableListener implements Listener
{
	private TabDeco plugin;
	public static VanishManager vManager;
	
	public PluginEnableListener(TabDeco plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event)
	{
		try
		{
			if(vManager == null)
			{
				Plugin vanish = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
		
		        if (vanish != null) 
		        {
		        	vManager = ((VanishPlugin)vanish).getManager();
		        	if(vManager != null)
		        	{
		        		TabDeco.debugLogger.info("VanishNoPacket hooked");
		        	}
		        }
			}
		}
		catch(Exception ex)
		{
			
		}
	}
}
