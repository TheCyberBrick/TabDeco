package TCB.TabDeco.API;

import org.bukkit.plugin.Plugin;

import TCB.TabDeco.TabDeco;

public class TabDecoRegistry 
{
	/**Registers a new setting
	 * 
	 * @param settingName The name of the setting in the configuration without []
	 * @param setting The class extending TabDecoSetting which contains the function that returns the text for the slot
	 * @param plugin The plugin
	 */
	public static boolean registerNewSetting(String settingName, TabDecoSetting setting, Plugin plugin)
	{
		try
		{
			if(setting != null)
			{
				boolean doesExist = false;
				for(int i = 0; i < TabDeco.externalSettings.size(); i++)
				{
					TabDecoSetting currentSetting = TabDeco.externalSettings.get(i);
					if(currentSetting.settingName.equals(settingName))
					{
						doesExist = true;
						break;
					}
				}
				if(!doesExist)
				{
					setting.settingName = settingName; 
					TabDeco.externalSettings.add(setting);
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Registered external setting from " + plugin.getName() + ": " + "[" + settingName + "]");
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			TabDeco.debugLogger.severe("Couldn't register: " + settingName + " from " + plugin.getName());
			TabDeco.debugLogger.severe(ex.getMessage());
		}
		return false;
	}
	
	/**Registers a new sorter
	 * 
	 * @param sorterName The name of the sorter in the configuration without {}
	 * @param sorter The class extending TabDecoSorter which contains the function that returns the sorted player list
	 * @param plugin The plugin
	 */
	public static boolean registerNewSorter(String sorterName, TabDecoSorter sorter, Plugin plugin)
	{
		try
		{
			if(sorter != null)
			{
				boolean doesExist = false;
				for(int i = 0; i < TabDeco.externalSorter.size(); i++)
				{
					TabDecoSorter currentSorter = TabDeco.externalSorter.get(i);
					if(currentSorter.sorterName.equals(sorterName))
					{
						doesExist = true;
						break;
					}
				}
				if(!doesExist)
				{
					sorter.sorterName = sorterName; 
					TabDeco.externalSorter.add(sorter);
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Registered external sorter from " + plugin.getName() + ": " + "[" + sorterName + "]");
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			TabDeco.debugLogger.severe("Couldn't register: " + sorterName + " from " + plugin.getName());
			TabDeco.debugLogger.severe(ex.getMessage());
		}
		return false;
	}
	
	/**Removes a setting.
	 * 
	 * @param settingName The name of the setting in the configuration without []
	 */
	public static boolean removeSetting(String settingName)
	{
		try
		{
			for(int i = 0; i < TabDeco.externalSettings.size(); i++)
			{
				TabDecoSetting setting = TabDeco.externalSettings.get(i);
				if(setting.settingName.equals(settingName))
				{
					TabDeco.externalSettings.remove(i);
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Removed external setting [" + settingName + "]");
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return false;
	}
	
	/**Removes a sorter.
	 * 
	 * @param sorterName The name of the setting in the configuration without {}
	 */
	public static boolean removeSorter(String sorterName)
	{
		try
		{
			for(int i = 0; i < TabDeco.externalSorter.size(); i++)
			{
				TabDecoSorter sorter = TabDeco.externalSorter.get(i);
				if(sorter.sorterName.equals(sorterName))
				{
					TabDeco.externalSorter.remove(i);
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Removed external sorter [" + sorterName + "]");
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return false;
	}
	
	/**Registers a handler for advanced tab handling. Only 1 external handler can be registered in total.
	 * 
	 * @param handlerName Name of the handler
	 * @param handler The class that extends TabDecoHandler
	 * @param plugin The plugin
	 */
	public static boolean registerTabDecoHandler(String handlerName, TabDecoHandler handler, TabDeco plugin, TabDeco tabDecoInstance)
	{
		try
		{
			if(TabDeco.tabDecoHandler == null)
			{
				TabDecoHandler finishedHandler = handler;
				finishedHandler.handlerName = handlerName;
				finishedHandler.plugin = plugin;
				finishedHandler.tabDecoInstance = tabDecoInstance;
				TabDeco.tabDecoHandler = finishedHandler;
				if(TabDeco.debugMode)TabDeco.debugLogger.info("Registered TabDeco handler " + handlerName + " for " + plugin.getName());
			}
			else
			{
				TabDeco.debugLogger.warning(TabDeco.tabDecoHandler.plugin.getName() + " already registered a handler. You can't register more than 1 handler!");
			}
		}
		catch(Exception ex)
		{
			
		}
		return false;
	}
}
