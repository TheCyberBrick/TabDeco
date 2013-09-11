package TCB.TabDeco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import TCB.TabDeco.API.TabDecoHandler;
import TCB.TabDeco.API.TabDecoSetting;
import TCB.TabDeco.API.TabDecoSorter;

public class TabDeco extends JavaPlugin
{
	public static TabDeco instance;
	public static BukkitTask refreshTask;
	public static SortingHelper sortingHelper;
	public static Logger debugLogger;
	
	/**Classes/Constructors/Methods/Fields**/
	private static Class<?> BukkitPlayerEntity;
	private static Class<?> EntityPlayer;
	private static Class<?> NetServerHandler;
	private static Class<?> Packet;
	private static Class<?> Packet201PlayerInfo;
	private static Class<?> Packet255KickDisconnect;
	private static Constructor<?> newPacket;
	private static Constructor<?> newPacket2;
	private static Method sendPacket;
	private static Field BukkitPlayerEntityEntity;
	private static Field entityPlayerNetServerHandler;
	private static String craftBukkitPackage = "org.bukkit.craftbukkit.";
	private static String minecraftPackage = "net.minecraft.server.";
	private static String version = "";

	/**Settings**/
	public static String listSorting = "{default}";
	public static String playerLayout = "[playername]";
	public static boolean checkForWorlds = false;
	public static int slotCount = 0;
	public static boolean timerEnabled = true;
	public static long timerSpeed = 80;
	public static boolean debugMode = false;
	public static boolean playerLayoutEnabled = false;
	public ArrayList<String> tabData;
	public static ArrayList<String> playerLayoutList;

	/**API External Settings**/
	public static TabDecoHandler tabDecoHandler;
	public static ArrayList<TabDecoSetting> externalSettings = new ArrayList<TabDecoSetting>();
	public static ArrayList<TabDecoSorter> externalSorter = new ArrayList<TabDecoSorter>();

	@Override 
	public void onEnable(){
		readAndInitTabData();
		sortingHelper = new SortingHelper(this);
		instance = this;
		debugLogger = getLogger();
		initFields();
		if(debugMode)debugLogger.info("Initialized all fields!");
		registerCommands();
		if(debugMode)debugLogger.info("Initialized Commands!");
		getServer().getPluginManager().registerEvents(new PlayerConnectListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerKillDeathListener(this), this);
		getServer().getPluginManager().registerEvents(new PluginEnableListener(this), this);
		if(debugMode)debugLogger.info("Initialized Listeners!");

		if(timerEnabled)
		{
			refreshTask = getServer().getScheduler().runTaskTimer(this, new Runnable() {
				@Override  
				public void run() {
					refreshTabData(true);
				}
			}, 0L, timerSpeed);
		}

		//TabDecoRegistry.registerNewSetting("testsetting", new TestSetting(), this);
		//TabDecoRegistry.registerTabDecoHandler("handlerTest", new TestHandler(), this, getInstance());
	}

	public void setData(String key, Object value) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder().getPath() + "/data.dat"));
		config.set(key, value);
		try {
			config.save(new File(getDataFolder().getPath() + "/data.dat"));
		} catch (Exception e) {}
	}
	
	public YamlConfiguration getData() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder().getPath() + "/data.dat"));
		return config;
	}

	@Override
	public void onDisable() 
	{
		if(refreshTask != null)
		{
			refreshTask.cancel();
			refreshTask = null;
		}
	}

	private static int uniqueEndingCounter = 0;
	private static int uniqueColorCounter = 0;

	public static void resetUniqueEnding() {
		uniqueColorCounter = 0;
		uniqueEndingCounter = 0;
	}

	public static String getUniqueEnding()
	{
		String uniqueEnding = "";
		for (int a = 0; a < uniqueEndingCounter; a++) 
		{
			uniqueEnding = " " + uniqueEnding;
		}
		uniqueEnding = "§" + getColorByInt(uniqueColorCounter) + uniqueEnding;
		uniqueColorCounter++;
		if(uniqueColorCounter >= 15)
		{
			uniqueColorCounter = 0;
			uniqueEndingCounter++;
		}
		return uniqueEnding;
	}

	public void registerCommands()
	{
		getCommand("td").setExecutor(new TabDecoCommandExecutor(this));
	}

	/**Loads the configuration data**/
	public void readAndInitTabData()
	{
		slotCount = this.getServer().getMaxPlayers();

		tabData = new ArrayList<String>();
		tabData.clear();
		playerLayoutList = new ArrayList<String>();
		playerLayoutList.clear();
		externalSettings = new ArrayList<TabDecoSetting>();
		externalSettings.clear();

		getDataFolder().mkdirs();
		String configPath = getDataFolder().getPath() + "/config.yml";
		try
		{
			File f = new File(configPath);
			if(f.exists())
			{
				//Reading configuration
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configPath), "UTF8"));;
				String line = br.readLine();

				boolean slotSettings = false;
				boolean optionSettings = false;

				while (line != null) {
					try
					{
						String currentLine = line;
						if(!currentLine.startsWith("#"))
						{
							if(currentLine.startsWith("options:"))
							{
								optionSettings = true;
							}
							if(currentLine.startsWith("slots:"))
							{
								slotSettings = true;
							}
							if(currentLine.startsWith("\t-") && optionSettings && !slotSettings)
							{
								if(currentLine.startsWith("\t- slotCount: "))
								{
									currentLine = currentLine.substring(14);
									if(!currentLine.contains("[serverslots]")){
										slotCount = Integer.parseInt(currentLine);
									}
								}
								if(currentLine.startsWith("\t- debugMode: "))
								{
									currentLine = currentLine.substring(14);
									debugMode = Boolean.parseBoolean(currentLine);
								}
								if(currentLine.startsWith("\t- timerEnabled: "))
								{
									currentLine = currentLine.substring(17);
									timerEnabled = Boolean.parseBoolean(currentLine);
								}
								if(currentLine.startsWith("\t- timerSpeed: "))
								{
									currentLine = currentLine.substring(15);
									timerSpeed = Long.parseLong(currentLine) * 20L;
								}
								if(currentLine.startsWith("\t- playerLayoutEnabled: "))
								{
									currentLine = currentLine.substring(24);
									playerLayoutEnabled = Boolean.parseBoolean(currentLine);
								}
								if(currentLine.startsWith("\t- playerLayout: "))
								{
									currentLine = currentLine.substring(17);
									playerLayout = currentLine;
									if(playerLayout.contains("[nextslot]")) {
										String split[] = playerLayout.split("\\[nextslot\\]");
										for(int i = 0; i < split.length; i++) {
											playerLayoutList.add(split[i]);
										}
									} else {
										playerLayoutList.add(playerLayout);
									}
								}
								if(currentLine.startsWith("\t- checkForWorlds: "))
								{
									currentLine = currentLine.substring(19);
									checkForWorlds = Boolean.parseBoolean(currentLine);
								}
								if(currentLine.startsWith("\t- listSorting: ")) {
									currentLine = currentLine.substring(16);
									listSorting = currentLine;
								}
							}
							if(currentLine.startsWith("\t-") && slotSettings)
							{
								currentLine = currentLine.substring(3);
								tabData.add(currentLine + getUniqueEnding());
							}
						}
					}
					catch(Exception ex)
					{
					}
					line = br.readLine();
				}

				br.close();
				getLogger().info("Loaded configuration");
				if(debugMode)debugLogger.info("Loaded configuration");
				if(debugMode)debugLogger.info("Timer: " + (timerEnabled ? "Enabled" : "Disabled"));
				if(debugMode)debugLogger.info("Timer Speed: " + timerSpeed);
				if(debugMode)debugLogger.info("Player Layout: " + (playerLayoutEnabled ? "Enabled" : "Disabled"));
				if(debugMode)debugLogger.info("Sorter: " + listSorting);
			}
			else
			{
				FileWriter outFile = new FileWriter(configPath);
				PrintWriter out = new PrintWriter(outFile);
				//Generating long config...
				out.println("# |================ TabDeco Config ===================|");
				out.println("# |                                                   |");
				out.println("# | The symbol '#' marks a line as comment            |");
				out.println("# | Use '&' for colors                                |");
				out.println("# |---------------------------------------------------|");
				out.println("# | You can't use words longer than 16 characters     |");
				out.println("# | [@] = Marks a line as 'to be updated'             |");
				out.println("# |---------------------------------------------------|");
				out.println("# | Use tab for the configuration                     |");
				out.println("# |---------------------------------------------------|");
				out.println("# | 1 line = 1 slot                                   |");
				out.println("# |------------------ Tab settings -------------------|");
				out.println("# | [drawhealth] = Draws the health of                |");
				out.println("# | the player graphically                            |");
				out.println("# | [currentplayers] = Currently online players       |");
				out.println("# | [maxplayers] = Max online players                 |");
				out.println("# | [rint(x,y)] = Returns a random integer            |");
				out.println("# | between the numbers x and y                       |");
				out.println("# | [serverip] = Server IP                            |");
				out.println("# | [rcolor] = Returns a random color                 |");
				out.println("# | [player] = The Player looking at the menu         |");
				out.println("# | [localmobkills] = Shows how many mobs the player  |");
				out.println("# | this session killed                               |");
				out.println("# | [localkills] = Shows how many players got killed  |");
				out.println("# | by the player viewing the tab menu this session   |");
				out.println("# | [localdeaths] = Shows how many times the player   |");
				out.println("# | viewing the tab menu died this session            |");
				out.println("# | [localK/D] = Shows the local KDR of the player    |");
				out.println("# | [globalmobkills] = Shows how many mobs the player |");
				out.println("# | ever killed                                       |");
				out.println("# | [globalkills] = Shows how many players the        |");
				out.println("# | player viewing the tab menu ever killed           |");
				out.println("# | [globaldeaths] = Shows how many times the player  |");
				out.println("# | viewing the tab menu ever died                    |");
				out.println("# | [globalK/D] = Shows the global KDR of the player  |");
				out.println("# | [time] = Current time                             |");
				out.println("# | [date] = Current date                             |");
				out.println("# | [exp] = Returns the total exp of the player       |");
				out.println("# | [level] = Returns the level of the player         |");
				out.println("# | [health] = Shows the health of the player         |");
				out.println("# | [food] = Returns the food level in numbers        |");
				out.println("# | [drawfoodbar] = Draws the food level of the player|");
				out.println("# | [air] = Returns how much air the player has left  |");
				out.println("# | [lifetime] = Shows the time of the player on the  |");
				out.println("# | server in hours, minutes and seconds              |");
				out.println("# | [world] = Returns the world name of the world of  |");
				out.println("# | the player                                        |");
				out.println("# | [ipadress] = Shows the IP adress of the player    |");
				out.println("# | [posx] = Player position x                        |");
				out.println("# | [posy] = Player position y                        |");
				out.println("# | [posz] = Player position z                        |");
				out.println("# | [homex] = Home (Bed) position x                   |");
				out.println("# | [homey] = Home (Bed) position y                   |");
				out.println("# | [homez] = Home (Bed) position z                   |");
				out.println("# | [gamemode] = Returns the gamemode of the player   |");
				out.println("# | [entityid] = Returns the entity id of the player  |");
				out.println("# |------------------ Player Layout ------------------|");
				out.println("# | [playername] = Returns the name of the player     |");
				out.println("# | (counts for every player, not only the player     |");
				out.println("# | viewing the player list)                          |");
				out.println("# | [nextslot] = Adds an additional slot for every    |");
				out.println("# | player in the list. The text after this word      |");
				out.println("# | will be displayed in a seperate slot. Can be      |");
				out.println("# | repeated as many times as you want.               |");
				out.println("# |------------------- List sorting ------------------|");
				out.println("# | {default} = Default player list. No sorting       |");
				out.println("# | {name} = Sorts the player list by player names    |");
				out.println("# | {world} = Sorts the player list by world names    |");
				out.println("# | {health} = Sorts the player list by highest health|");
				out.println("# | {level} = Sorts the player list by highest level  |");
				out.println("# | {air} = Sorts the player list by most air         |");
				out.println("# | {exp} = Sorts the player list by highest exp      |");
				out.println("# | {entityid} = Sorts the player list by highest     |");
				out.println("# | entity ID                                         |");
				out.println("# | {localkills} = Sorts the player list by most local|");
				out.println("# | kills                                             |");
				out.println("# | {lcoaldeaths} = Sorts the player list by most     |");
				out.println("# | local deaths                                      |");
				out.println("# | {localK/D} = Sorts the player list by highest     |");
				out.println("# | local KDR                                         |");
				out.println("# | {globalkills} = Sorts the player list by most     |");
				out.println("# | global kills                                      |");
				out.println("# | {globaldeaths} = Sorts the player list by most    |");
				out.println("# | global deaths                                     |");
				out.println("# | {globalK/D} = Sorts the player list by highest    |");
				out.println("# | global KDR                                        |");
				out.println("# | {hunger} = Sorts the player list by least hunger  |");
				out.println("# | {lifetime} = Sorts the player list by longest     |");
				out.println("# | time on server                                    |");
				out.println("# |===================================================|");
				out.println();
				out.println("options:");
				out.println("	- debugMode: false");
				out.println("# The timer that updates the slots");
				out.println("	- timerEnabled: true");
				out.println("# The timer interval in seconds");
				out.println("	- timerSpeed: 4");
				out.println("# Custom player layout");
				out.println("	- playerLayoutEnabled: false");
				out.println("	- playerLayout: [playername]");
				out.println("# Makes the player list world specific");
				out.println("	- checkForWorlds: false");
				out.println("# Returns how the player list should be sorted");
				out.println("	- listSorting: {default}");
				out.println();
				out.println("# Add here your slots");
				out.println("slots:");
				out.println("	- TabDeco");
				out.close();
				getLogger().info("Created configuration. Please restart your server");
			}
		}
		catch(Exception ex)
		{
		}
	}

	/**Refreshes all the slots in the tab menu instantly
	 * 
	 * @param flag If the method should call external settings
	 */
	public void refreshTabData(boolean flag)
	{
		if(tabDecoHandler != null)
		{
			tabDecoHandler.refreshTabData();
			return;
		}

		for(int i = 0; i < TabDeco.getOnlinePlayerCount(); i++)
		{
			if(TabDeco.debugMode)TabDeco.debugLogger.info("---------------- Removing ----------------");
			//Removing old text
			Player player = getServer().getOnlinePlayers()[i];
			ArrayList<String> playerTabData = this.getPlayerData(player, "playerTabData");
			boolean needsUpdate = false;

			if(playerTabData != null) {
				for(String ptd : playerTabData) {
					sendSpecificPacketToPlayer(player, false, ptd.replaceAll("\\[\\@\\]", ""));
					needsUpdate = true;
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Removed text: " + ptd.replaceAll("\\[\\@\\]", ""));
				}
			} else {
				needsUpdate = true;
			}

			if(!needsUpdate) {
				break;
			}

			//Removing all players
			displayAllPlayerForPlayer(player, false, true);

			if(TabDeco.debugMode)TabDeco.debugLogger.info("------------------------------------------");
			if(TabDeco.debugMode)TabDeco.debugLogger.info("----------------- Adding -----------------");

			//Adding new text
			ArrayList<String> lastTabData = new ArrayList<String>();
			for(int c = 0; c < this.tabData.size(); c++) {
				String newText = this.tabData.get(c);
				//Checks if it needs to be updated
				if(newText.contains("[@]")) {
					//Returning new text
					newText = replaceAllWords(newText, player, true);
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Added text: " + newText.replaceAll("\\[\\@\\]", ""));
				} else {
					//Returning old text
					if(playerTabData != null) {
						if(playerTabData.size() > c) {
							newText = playerTabData.get(c);
						}
					}
					if(TabDeco.debugMode)TabDeco.debugLogger.info("Not updating. Adding old text: " + newText);
				}
				//Sends the packet
				sendSpecificPacketToPlayer(player, true, newText.replaceAll("\\[\\@\\]", ""));
				lastTabData.add(newText);
			}

			//Setting last tab data
			this.setPlayerData(player, "playerTabData", lastTabData);

			//Adding all players
			displayAllPlayerForPlayer(player, true, true);
			if(TabDeco.debugMode)TabDeco.debugLogger.info("------------------------------------------");
		}
	}

	/**Adds/Removes all player names from the tab list for a player
	 * 
	 * @param player The player to send the packets to
	 * @param flag Add/Remove all players from tab menu
	 * @param flag2 If the method should call external settings
	 */
	public void displayAllPlayerForPlayer(Player player, boolean flag, boolean flag2)
	{
		if(tabDecoHandler != null)
		{
			tabDecoHandler.displayAllPlayerForPlayer(player, flag);
			return;
		}

		if(playerLayoutEnabled) {

			//Checking if add or remove
			if(flag) {
				//Resetting ending counter
				resetUniqueEnding();
				//Getting (un)sorted player list
				ArrayList<Player> playerList = this.sortList();
				for(Player playerToAdd : playerList) {
					//Checking if player is online
					if(playerToAdd.isOnline()) {
						ArrayList<String> lastAdditionalSlots = new ArrayList<String>();
						for(int c = 0; c < playerLayoutList.size(); c++) {
							String text = playerLayoutList.get(c);
							//Getting additional slots
							ArrayList<String> lastPlayerLayoutList = this.getPlayerData(playerToAdd, "additionalSlots");
							//Checking if text needs to be updated
							if(text.contains("[@]") || lastPlayerLayoutList == null) {
								//Replacing/Updating words
								text = this.replaceAllWords(text, playerToAdd, true);
							} else {
								if(lastPlayerLayoutList != null) {
									if(lastPlayerLayoutList.size() > c) {
										//Getting old text/not updating
										text = lastPlayerLayoutList.get(c);
									}
								}
							}
							//Adding unique ending/Making text unique
							if(!text.contains(player.getPlayerListName()) && !text.contains(playerToAdd.getPlayerListName()))text = text + getUniqueEnding();
							//Adding to old list
							lastAdditionalSlots.add(text);
							//Sending packet
							sendSpecificPacketToPlayer(player, true, text.replaceAll("\\[\\@\\]", ""));
							if(TabDeco.debugMode)TabDeco.debugLogger.info("Adding additional slot for " + player.getDisplayName() + " (length: " + text.replaceAll("\\[\\@\\]", "").length() + "): " + text.replaceAll("\\[\\@\\]", ""));
						}

						//Creating composed list
						ArrayList<String> composedList = lastAdditionalSlots;
						//Getting last list
						ArrayList<String> lastPlayerLayoutList = this.getPlayerData(playerToAdd, "additionalSlots");
						if(lastPlayerLayoutList != null) {
							for(String toAdd : lastPlayerLayoutList) {
								//Checking if composedList.size() < 20
								if(composedList.size() < 20) {
									//Adding to composedList
									composedList.add(toAdd);
								}
							}
						}

						//Setting old list
						this.setPlayerData(playerToAdd, "additionalSlots", composedList);
					}
				}
			} else {
				//Removing all players
				for(int i = 0; i < TabDeco.getOnlinePlayerCount(); i++) {
					Player playerToRemove = getServer().getOnlinePlayers()[i];
					//Checking if player is online
					if(playerToRemove.isOnline()) {
						//Sending packet
						this.removePlayer(player, playerToRemove);
					}
					ArrayList<String> lastPlayerLayoutList = this.getPlayerData(playerToRemove, "additionalSlots");
					if(lastPlayerLayoutList != null) {
						//Getting additional slots/removing additional slots
						for(String additionalSlot : lastPlayerLayoutList) {
							//Sending packet
							sendSpecificPacketToPlayer(player, false, additionalSlot.replaceAll("\\[\\@\\]", ""));
						}
					}
				}
			}

		} else {

			if(flag) {
				//Adding all players
				ArrayList<Player> playerList = this.sortList();
				for(Player playerToAdd : playerList) {
					if(playerToAdd.isOnline()) {
						//Sending packet
						TabDeco.sendPacketData(player, playerToAdd.getPlayerListName(), true, 10);
					}
				}
			} else {
				//Removing all players
				for(int i = 0; i < TabDeco.getOnlinePlayerCount(); i++) {
					Player playerToRemove = getServer().getOnlinePlayers()[i];
					if(playerToRemove.isOnline()) {
						//Sending packet
						this.removePlayer(player, playerToRemove);
					}
				}
			}

		}
	}

	public void removePlayer(Player player, Player playerToRemove) {
		String playerListName = playerToRemove.getPlayerListName();
		String playerName = playerToRemove.getName();
		String playerDispName = playerToRemove.getDisplayName();
		for(int x = 16 - playerListName.length(); x > 0; x--) {
			playerListName = playerListName + " ";
		}
		for(int x = 16 - playerName.length(); x > 0; x--) {
			playerName = playerName + " ";
		}
		for(int x = 16 - playerDispName.length(); x > 0; x--) {
			playerDispName = playerDispName + " ";
		}
		TabDeco.sendPacketData(player, playerListName, false, 10);
		TabDeco.sendPacketData(player, playerName, false, 10);
		TabDeco.sendPacketData(player, playerDispName, false, 10);
		TabDeco.sendPacketData(player, playerToRemove.getPlayerListName(), false, 10);
		TabDeco.sendPacketData(player, playerToRemove.getName(), false, 10);
		TabDeco.sendPacketData(player, playerToRemove.getDisplayName(), false, 10);
	}
	
	public ArrayList<Player> sortList() {
		if(listSorting.equals("{default}")) {
			if(debugMode)debugLogger.info("Sorted by default");
			return playerList();
		} else if(listSorting.equals("{name}")) {
			if(debugMode)debugLogger.info("Sorted by name");
			return sortingHelper.sortListByPlayerName();
		} else if(listSorting.equals("{world}")) {
			if(debugMode)debugLogger.info("Sorted by world");
			return sortingHelper.sortListByWorld();
		} else if(listSorting.equals("{localK/D}")) {
			if(debugMode)debugLogger.info("Sorted by local KDR");
			return sortingHelper.sortListByLocalKD();
		} else if(listSorting.equals("{globalK/D}")) {
			if(debugMode)debugLogger.info("Sorted by global KDR");
			return sortingHelper.sortListByGlobalKD();
		} else if(listSorting.equals("{health}")) {
			if(debugMode)debugLogger.info("Sorted by health");
			return sortingHelper.sortListByHealth();
		} else if(listSorting.equals("{level}")) {
			if(debugMode)debugLogger.info("Sorted by level");
			return sortingHelper.sortListByLevel();
		} else if(listSorting.equals("{air}")) {
			if(debugMode)debugLogger.info("Sorted by air");
			return sortingHelper.sortListByAir();
		} else if(listSorting.equals("{exp}")) {
			if(debugMode)debugLogger.info("Sorted by EXP");
			return sortingHelper.sortListByEXP();
		} else if(listSorting.equals("{entityid}")) {
			if(debugMode)debugLogger.info("Sorted by entity ID");
			return sortingHelper.sortListByID();
		} else if(listSorting.equals("{localkills}")) {
			if(debugMode)debugLogger.info("Sorted by local kills");
			return sortingHelper.sortListByLocalKills();
		} else if(listSorting.equals("{globalkills}")) {
			if(debugMode)debugLogger.info("Sorted by global kills");
			return sortingHelper.sortListByGlobalKills();
		} else if(listSorting.equals("{localdeaths}")) {
			if(debugMode)debugLogger.info("Sorted by local deaths");
			return sortingHelper.sortListByLocalDeath();
		} else if(listSorting.equals("{globaldeaths}")) {
			if(debugMode)debugLogger.info("Sorted by global deaths");
			return sortingHelper.sortListByGlobalDeath();
		} else if(listSorting.equals("{lifetime}")) {
			if(debugMode)debugLogger.info("Sorted by time on server");
			return sortingHelper.sortListByLifeTime();
		} else if(listSorting.equals("{hunger}")) {
			if(debugMode)debugLogger.info("Sorted by hunger");
			return sortingHelper.sortListByHunger();
		}

		//Sorter API
		for(TabDecoSorter sorter : externalSorter) {
			if(listSorting.equals("{" + sorter.sorterName + "}")) {
				//Get external sorter/sort list
				if(debugMode)debugLogger.info("Sorted by external sorter " + sorter.sorterName);
				return sorter.sortPlayerList(this.playerList());
			}
		}

		if(debugMode)debugLogger.info("Sorted by -");
		return playerList();
	}

	//Getting the player arraylist
	public ArrayList<Player> playerList() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < TabDeco.getOnlinePlayerCount(); i++) {
			Player player = getServer().getOnlinePlayers()[i];
			playerList.add(player);
		}
		return playerList;
	}

	//Setting player metadata
	public static void setMetadata(Player player, String key, Object value, Plugin plugin){
		player.setMetadata(key, new FixedMetadataValue(plugin,value));
	}

	//Getting player metadata
	public static Object getMetadata(Player player, String key, Plugin plugin){
		List<MetadataValue> values = player.getMetadata(key);  
		for(MetadataValue value : values){
			if(value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())){
				return value.value();
			}
		}
		return null;
	}

	//Setting player data directly
	public void setPlayerData(Player player, String dataID, ArrayList<String> list) {
		setMetadata(player, dataID, new FixedMetadataValue(this, list), this);
	}

	@SuppressWarnings("unchecked")
	//Getting player data directly
	public ArrayList<String> getPlayerData(Player player, String dataID) {
		FixedMetadataValue metadata = (FixedMetadataValue)getMetadata(player, dataID, this);
		if(metadata != null) {
			if(metadata.value() instanceof ArrayList<?>) {
				return (ArrayList<String>)metadata.value();
			}
		}
		return null;
	}

	/**Replaces the words in the array tabData with the text of the slots
	 * 
	 * @param text The input text
	 * @param player The player viewing the tab menu
	 * @param flag If the method should call external settings
	 */
	public String replaceAllWords(String text, Player player, boolean flag)
	{
		if(tabDecoHandler != null)
		{
			tabDecoHandler.replaceAllWords(text, player);
		}
		else
		{
			text = text.replaceAll("\\[currentplayers\\]", "" + getOnlinePlayerCount());
			text = text.replaceAll("\\[maxplayers\\]", "" + getServer().getMaxPlayers());
			text = text.replaceAll("\\[serverip\\]", "" + getServer().getIp());
			text = text.replaceAll("\\[player\\]", player.getName());
			text = text.replaceAll("\\[playername\\]", player.getName());
			text = text.replaceAll("\\[rcolor\\]", randomColor());
			text = text.replaceAll("\\[drawhealth\\]", getPlayerHealthString(player));
			text = text.replaceAll("\\[localmobkills\\]", "" + getLocalMobKills(player));
			text = text.replaceAll("\\[localkills\\]", "" + getLocalPlayerKills(player));
			text = text.replaceAll("\\[localdeaths\\]", "" + getLocalPlayerDeaths(player));
			text = text.replaceAll("\\[localK\\/D\\]", "" + roundNumber(getPlayerKD(getLocalPlayerKills(player), getLocalPlayerDeaths(player)), 2));
			text = text.replaceAll("\\[globalmobkills\\]", "" + getGlobalMobKills(player));
			text = text.replaceAll("\\[globalkills\\]", "" + getGlobalPlayerKills(player));
			text = text.replaceAll("\\[globaldeaths\\]", "" + getGlobalPlayerDeaths(player));
			text = text.replaceAll("\\[globalK\\/D\\]", "" + roundNumber(getPlayerKD(getGlobalPlayerKills(player), getGlobalPlayerDeaths(player)), 2));
			text = text.replaceAll("\\[exp\\]", "" + player.getTotalExperience());
			text = text.replaceAll("\\[level\\]", "" + player.getLevel());
			text = text.replaceAll("\\[health\\]", "" + ((Damageable)player).getHealth());
			text = text.replaceAll("\\[maxhealth\\]", "" + ((Damageable)player).getMaxHealth());
			text = text.replaceAll("\\[food\\]", "" + player.getFoodLevel());
			text = text.replaceAll("\\[drawfoodbar\\]", "" + getPlayerFoodBarString(player));
			text = text.replaceAll("\\[air\\]", "" + (player.getRemainingAir() / player.getMaximumAir() * 10));
			text = text.replaceAll("\\[lifetime\\]", getLifeTime(player));
			text = text.replaceAll("\\[world\\]", player.getWorld().getName());
			text = text.replaceAll("\\[ipadress\\]", player.getAddress().getHostString());
			text = text.replaceAll("\\[posx\\]", "" + roundNumber(player.getLocation().getX(), 2));
			text = text.replaceAll("\\[posy\\]", "" + roundNumber(player.getLocation().getY(), 2));
			text = text.replaceAll("\\[posz\\]", "" + roundNumber(player.getLocation().getZ(), 2));
			if(player.getBedSpawnLocation() != null)
			{
				text = text.replaceAll("\\[homex\\]", "" + roundNumber(player.getBedSpawnLocation().getX(), 2));
				text = text.replaceAll("\\[homey\\]", "" + roundNumber(player.getBedSpawnLocation().getY(), 2));
				text = text.replaceAll("\\[homez\\]", "" + roundNumber(player.getBedSpawnLocation().getZ(), 2));
			}
			text = text.replaceAll("\\[entityid\\]", "" + player.getEntityId());
			text = text.replaceAll("\\[gamemode\\]", player.getGameMode().name());


			text = replaceRInt(text);


			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			text = text.replaceAll("\\[time\\]", "" + sdf.format(cal.getTime()));


			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			text = text.replaceAll("\\[date\\]", "" + dateFormat.format(date));
		}



		/**Replacing external settings**/
		try
		{
			for(int i = 0; i < externalSettings.size(); i++)
			{
				TabDecoSetting setting = externalSettings.get(i);
				if(setting != null)
				{
					if(setting.settingName.length() > 0)
					{
						String settingName = "\\[" + setting.settingName + "\\]";
						String replacedString = setting.getSlotText(player, text, setting.settingName);
						if(replacedString != null)
						{
							text = text.replaceAll(settingName, replacedString);
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			if(TabDeco.debugMode)debugLogger.severe("Failed to load external data");
			if(TabDeco.debugMode)ex.printStackTrace();
		}

		return text;
	}

	public static String getLifeTime(Player player)
	{
		int totalSeconds = player.getTicksLived() / 20;
		int hours = totalSeconds / 3600,
				remainder = totalSeconds % 3600,
				minutes = remainder / 60,
				seconds = remainder % 60;

		return ((hours < 10 ? "0" : "") + hours
				+ ":" + (minutes < 10 ? "0" : "") + minutes
				+ ":" + (seconds< 10 ? "0" : "") + seconds);
	}

	public static double roundNumber(double number, int stellen)
	{
		return Math.round(number*Math.pow(10, stellen))/Math.pow(10, stellen);
	}

	/**Returns the K/D rate
	 * 
	 * @param k Kill count
	 * @param d Death count
	 */
	public double getPlayerKD(int k, int d)
	{
		if(d != 0)
		{
			return (double)((double)k / (double)d);
		}
		else
		{
			if(k == 0)
			{
				return 1D;
			}
			else
			{
				return (double)k;
			}
		}
	}

	/**Returns the local kill counter of a player**/
	public int getLocalPlayerKills(Player player)
	{
		FixedMetadataValue metadata = (FixedMetadataValue)getMetadata(player, "killCounter", this);
		return (metadata != null) ? metadata.asInt() : 0;
	}

	/**Returns the local death counter of a player**/
	public int getLocalPlayerDeaths(Player player)
	{
		FixedMetadataValue metadata = (FixedMetadataValue)getMetadata(player, "deathCounter", this);
		return (metadata != null) ? metadata.asInt() : 0;
	}
	
	/**Returns the local mob kills of a player**/
	public int getLocalMobKills(Player player)
	{
		FixedMetadataValue metadata = (FixedMetadataValue)getMetadata(player, "mobkills", this);
		return (metadata != null) ? metadata.asInt() : 0;
	}
	
	/**Returns the global kill counter of a player**/
	public int getGlobalPlayerKills(Player player)
	{
		if(this.getData() != null) {
			return this.getData().getInt("player." + player.getName() + ".kills");
		}
		return 0;
	}

	/**Returns the global death counter of a player**/
	public int getGlobalPlayerDeaths(Player player)
	{
		if(this.getData() != null) {
			return this.getData().getInt("player." + player.getName() + ".deaths");
		}
		return 0;
	}
	
	/**Returns the global mob kills of a player**/
	public int getGlobalMobKills(Player player)
	{
		if(this.getData() != null) {
			return this.getData().getInt("player." + player.getName() + ".mobkills");
		}
		return 0;
	}

	/**Returns a graphical health bar
	 * 
	 * @param player The player viewing the tab menu
	 */
	public String getPlayerHealthString(Player player)
	{
		String healthString = "&2";
		for(int i = 0; i < 10; i++)
		{
			if(i == ((((Damageable)player).getHealth() + 1) / 2))
			{
				healthString += "&4";
			}
			healthString += "┃";
		}
		return healthString;
	}

	/**Returns a graphical food bar
	 * 
	 * @param player The player viewing the tab menu
	 */
	public String getPlayerFoodBarString(Player player)
	{
		String healthString = "&2";
		for(int i = 0; i < 10; i++)
		{
			if(i == ((player.getFoodLevel() + 1) / 2))
			{
				healthString += "&4";
			}
			healthString += "┃";
		}
		return healthString;
	}

	/**Adds/Removes a player name from the tab list
	 * 
	 * @param player The player to send the packet to
	 * @param flag Add/Remove player from tab menu
	 */
	public void displayPlayer(Player player, boolean flag)
	{
		TabDeco.sendPacketData(player, player.getPlayerListName(), flag, 10);
	}

	//Comparing worlds/Checking if worlds are equal
	public static boolean compareWorlds(Player player, String otherPlayerName)
	{
		if(!checkForWorlds)
		{
			return true;
		}
		Player otherPlayer = getPlayerByName(otherPlayerName);
		if(otherPlayer != null)
		{
			if(otherPlayer.isOnline())
			{
				if(player.getWorld().equals(otherPlayer.getWorld()))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	public static String replaceRInt(String text)
	{
		return text.replaceAll("\\[rint\\([0-9]{1,}\\,[0-9]{1,}\\)\\]", ""+getRandomNumber(getRInt1(text), getRInt2(text)));
	}

	public static int getRInt1(String text)
	{
		String randomReg = "[0-9]{1,}";
		Pattern regex = Pattern.compile("\\[rint\\(" + randomReg + "\\," + randomReg + "\\)\\]");
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) 
		{
			String firstInt = regexMatcher.group().replaceAll("\\," + randomReg + "\\)\\]", "").replaceAll("\\[rint\\(", "");
			try
			{
				return Integer.parseInt(firstInt);
			}
			catch(Exception ex)
			{

			}
		} 
		return 0;
	}

	public static int getRInt2(String text)
	{
		String randomReg = "[0-9]{1,}";
		Pattern regex = Pattern.compile("\\[rint\\(" + randomReg + "\\," + randomReg + "\\)\\]");
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) 
		{
			String secondInt = regexMatcher.group().replaceAll("\\)\\]", "").replaceAll("\\[rint\\(" + randomReg + "\\,", "");
			try
			{
				return Integer.parseInt(secondInt);
			}
			catch(Exception ex)
			{

			}
		} 
		return 0;
	}

	/**Returns a random number between min and max
	 * 
	 * @param min Minimum number
	 * @param max Maximum number
	 * @return
	 */
	public static int getRandomNumber(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
	}

	public String randomColor()
	{
		String color;
		Random rand = new Random();
		int colorInt = rand.nextInt(16);
		color = "" + colorInt;
		if(colorInt == 10)color = "a";
		if(colorInt == 11)color = "b";
		if(colorInt == 12)color = "c";
		if(colorInt == 13)color = "d";
		if(colorInt == 14)color = "e";
		if(colorInt == 15)color = "f";

		return "&" + color;
	}

	public static String getColorByInt(int i)
	{
		String color;
		int colorInt = i;
		color = "" + colorInt;
		if(colorInt == 10)color = "a";
		if(colorInt == 11)color = "b";
		if(colorInt == 12)color = "c";
		if(colorInt == 13)color = "d";
		if(colorInt == 14)color = "e";
		if(colorInt == 15)color = "f";

		return color;
	}

	/**Sends the packet Packet201PlayerInfo to a player
	 * 
	 * @param player The player to send the packet to
	 * @param text The text to send with the packet
	 * @param flag Add/Remove from tab list
	 * @param ping Ping to the "text"
	 */
	public static void sendPacketData(Player player, String text, boolean flag, int ping) 
	{
		try
		{
			Object targetEntity = player;
			Object minecraftServerPlayer = BukkitPlayerEntityEntity.get(targetEntity);
			Object netSenderHandler = entityPlayerNetServerHandler.get(minecraftServerPlayer);
			text = text.substring(0, 16);
			Object dataPacket = newPacket.newInstance(new Object[] {text, flag, ping});
			sendPacket.invoke(netSenderHandler, new Object[] {dataPacket});
		} 
		catch (Exception e)
		{
		}
	}

	/**Sends the packet Packet255KickDisconnect for custom slot amount
	 * 
	 * @param player The player
	 * @param text The text for the packet
	 */
	public static void sendPacketDataKickDisconnect(Player player, String text) 
	{
		try
		{
			Object targetEntity = player;
			Object minecraftServerPlayer = BukkitPlayerEntityEntity.get(targetEntity);
			Object netSenderHandler = entityPlayerNetServerHandler.get(minecraftServerPlayer);
			Object dataPacket2 = newPacket2.newInstance(new Object[] {text});
			sendPacket.invoke(netSenderHandler, new Object[] {dataPacket2});
		} 
		catch (Exception e)
		{
		}
	}

	/**Sends the packet Packet201PlayerInfo to a player
	 * 
	 * @param player The player to send the packet to
	 * @param state Add/Remove from tab list
	 * @param text The text to send with the packet
	 */
	public void sendSpecificPacketToPlayer(Player player, boolean state, String text) 
	{
		String transformedText = ChatColor.translateAlternateColorCodes('&', text);
		if (transformedText.length() > 16) {
			String prevTransformedText = transformedText;
			transformedText = transformedText.substring(0, 16);
			if(transformedText.substring(15).contains("§")) {
				transformedText = transformedText.substring(0, 15) + transformedText.substring(15).replaceAll("§", "");
			}
			if(TabDeco.debugMode)getLogger().warning("The text: " + prevTransformedText + " is too long! Shortened to: " + transformedText);
		}
		for(int i = 16 - transformedText.length(); i > 0; i--) {
			transformedText = transformedText + " ";
		}
		sendPacketData(player, transformedText, state, 10);
	}

	/**Returns how many players are online**/
	public static int getOnlinePlayerCount()
	{
		return Bukkit.getOnlinePlayers().length;
	}

	/**Returns the player with the name**/
	public static Player getPlayerByName(String name)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getName().equals(name))
			{
				return p;
			}
		}
		return null;
	}

	public static void initFields()
	{
		String[] versions = getServerVesions();
		if (versions.length == 5) 
		{
			version = versions[3];
			craftBukkitPackage = "org.bukkit.craftbukkit." + version + ".";
			minecraftPackage = "net.minecraft.server." + version + ".";
		}

		reflectAllClasses();
		reflectMisc();
	}

	/**Returns the server version**/
	public static String[] getServerVesions()
	{
		try
		{
			Object bukkitServer = Bukkit.getServer();
			Method handle = bukkitServer.getClass().getMethod("getHandle", new Class[0]);
			Object o = handle.invoke(bukkitServer, new Object[0]);
			String className = o.getClass().getName();
			String[] versions = className.split("\\.");
			return versions;
		}
		catch (Exception ex) 
		{
			TabDeco.debugLogger.severe("Couldn't load server version!");
		}
		return null;
	}

	public static void reflectAllClasses()
	{
		try
		{
			NetServerHandler = reflectMinecraftServerClass("PlayerConnection");
			Packet = reflectMinecraftServerClass("Packet");
			Packet201PlayerInfo = reflectMinecraftServerClass("Packet201PlayerInfo");
			Packet255KickDisconnect = reflectMinecraftServerClass("Packet255KickDisconnect");
			BukkitPlayerEntity = reflectBukkitServerClass("entity.CraftEntity");
			EntityPlayer = reflectMinecraftServerClass("EntityPlayer");
		}
		catch(Exception ex)
		{

		}
	}

	public static void reflectMisc()
	{
		try
		{
			BukkitPlayerEntityEntity = BukkitPlayerEntity.getDeclaredField("entity");
			BukkitPlayerEntityEntity.setAccessible(true);
			entityPlayerNetServerHandler = EntityPlayer.getField("playerConnection");
			sendPacket = NetServerHandler.getMethod("sendPacket", new Class[] { Packet });
			newPacket = Packet201PlayerInfo.getConstructor(new Class[] { String.class, Boolean.TYPE, Integer.TYPE });
			newPacket2 = Packet255KickDisconnect.getConstructor(new Class[] {String.class});
		}
		catch(Exception ex)
		{

		}
	}

	/**Returns a class of the minecraft server by name
	 * 
	 * @param classname The name of the class
	 * @return
	 * @throws Exception
	 */
	private static Class<?> reflectMinecraftServerClass(String classname) throws Exception
	{
		Class<?> minecraftServerClass = Class.forName(minecraftPackage + classname);
		if(minecraftServerClass == null)
		{
			TabDeco.debugLogger.severe("Class " + (minecraftPackage + classname) + " not found! Wrong version?");
		}
		return minecraftServerClass;
	}

	/**Returns a class of the bukkit server by name
	 * 
	 * @param classname The name of the class
	 * @return
	 * @throws Exception
	 */
	private static Class<?> reflectBukkitServerClass(String classname) throws Exception 
	{
		Class<?> craftBukkitServerClass = Class.forName(craftBukkitPackage + classname);
		if(craftBukkitServerClass == null)
		{
			TabDeco.debugLogger.severe("Class " + (craftBukkitPackage + classname) + " not found! Wrong version?");
		}
		return craftBukkitServerClass;
	}
}