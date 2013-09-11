package TCB.TabDeco.API;

import org.bukkit.entity.Player;

import TCB.TabDeco.TabDeco;


public abstract class TabDecoHandler
{
	public String handlerName;
	public TabDeco plugin;
	public TabDeco tabDecoInstance;
	
	public TabDecoHandler(TabDeco plugin) {
		this.plugin = plugin;
	}
	
	/**Returns the name of the handler**/
	public String getHandlerName()
	{
		return this.handlerName;
	}
	
	/**This function gets called if the tab list updates. Called by the update timer/command**/
	public void refreshTabData()
	{
		this.plugin.refreshTabData(false);
	}
	
	/**This function gets called to replace all the settings [] with the actual text for the slots**/
	public String replaceAllWords(String text, Player player)
	{
		return this.plugin.replaceAllWords(text, player, false);
	}

	/**This function adds/removes all players from the tab list
	 * 
	 * @param player The player to send the packet to
	 * @param flag Add/Remove
	 */
	public void displayAllPlayerForPlayer(Player player, boolean flag)
	{
		this.plugin.displayAllPlayerForPlayer(player, flag, false);
	}
}
