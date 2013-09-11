package TCB.TabDeco;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TabDecoCommandExecutor implements CommandExecutor
{
	private TabDeco plugin;
	
	public TabDecoCommandExecutor(TabDeco plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("td"))
		{
			if(args.length > 0)
			{
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("debug"))
					{
						TabDeco.debugMode = !TabDeco.debugMode;
						sender.sendMessage("[TabDeco] " + (TabDeco.debugMode ? "Enabled" : "Disabled") + " debug mode.");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("refresh"))
					{
						plugin.refreshTabData(true);
						sender.sendMessage("[TabDeco] Refreshed tab objects.");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("resetmobkills"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							plugin.setData("player." + playerName + ".mobkills", null);
						}
						
						sender.sendMessage("[TabDeco] Reset all mob kills.");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("resetplayerkills"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							plugin.setData("player." + playerName + ".kills", null);
						}
							
						sender.sendMessage("[TabDeco] Reset all player kills.");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("resetdeaths"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							plugin.setData("player." + playerName + ".deaths", null);
						}
							
						sender.sendMessage("[TabDeco] Reset all deaths.");
						return true;
					}
				} else {
					if(args[0].equalsIgnoreCase("resetmobkills"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							if(playerName.equalsIgnoreCase(args[1])) {
								plugin.setData("player." + playerName + ".mobkills", null);
								sender.sendMessage("[TabDeco] Reset all mob kills for " + playerName);
								return true;
							}
						}
					}
					
					if(args[0].equalsIgnoreCase("resetplayerkills"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							if(playerName.equalsIgnoreCase(args[1])) {
								plugin.setData("player." + playerName + ".kills", null);
								sender.sendMessage("[TabDeco] Reset all player kills for " + playerName);
								return true;
							}
						}
					}
					
					if(args[0].equalsIgnoreCase("resetdeaths"))
					{
						for(int i = 0; i < plugin.getData().getConfigurationSection("player").getKeys(false).toArray().length; i++)
						{
							String playerName = plugin.getData().getConfigurationSection("player").getKeys(false).toArray()[i].toString();
							if(playerName.equalsIgnoreCase(args[1])) {
								plugin.setData("player." + playerName + ".deaths", null);
								sender.sendMessage("[TabDeco] Reset all deaths for " + playerName);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
