package TCB.TabDeco.API;

import org.bukkit.entity.Player;

public abstract class TabDecoSetting 
{
	public String settingName;
	/**Returns the text in the slot
	 * 
	 * @param player The player viewing the tab menu
	 * @param inputText The input text before replacing
	 * @param settingName The name of the setting in the configuration without []
	 */
	public abstract String getSlotText(Player player, String inputText, String settingName);
	
	/**Returns the name of the setting in the configuration without []**/
	public String getName()
	{
		return this.settingName;
	}
}
