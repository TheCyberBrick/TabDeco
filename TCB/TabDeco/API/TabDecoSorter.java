package TCB.TabDeco.API;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public abstract class TabDecoSorter 
{
	public String sorterName;
	/**Returns the text in the slot
	 * 
	 * @param playerList The input player list
	 */
	public abstract ArrayList<Player> sortPlayerList(ArrayList<Player> playerList);
	
	/**Returns the name of the setting in the configuration without []**/
	public String getName()
	{
		return this.sorterName;
	}
}
