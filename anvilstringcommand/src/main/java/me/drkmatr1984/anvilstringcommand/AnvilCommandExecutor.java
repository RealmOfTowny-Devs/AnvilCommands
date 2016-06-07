package me.drkmatr1984.anvilstringcommand;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.drkmatr1984.anvilstringcommand.AnvilGUI.AnvilSlot;
import me.drkmatr1984.anvilstringcommand.tasks.CommandTask;

public class AnvilCommandExecutor
  implements CommandExecutor
{
  final String close = "[close]";
  final String nothing = "[nothing]";
  final String variable = "%userinput%";
  final String userVariable = "%player%";
  
  AnvilStringCommand plugin;
  AnvilStringConfig cfg;
  AnvilLang lang;
  String GUIinput = "";
  Player p = null;
  
  public AnvilCommandExecutor(AnvilStringCommand anvilStringCommand) {
    this.plugin = anvilStringCommand;
    this.lang = this.plugin.lang;
    this.lang.InitializeLang();
    this.cfg = this.plugin.config;
  }
  
  public boolean onCommand(CommandSender sender1, Command cmd, String label, String[] args)
  {
    CommandSender sender = sender1;
    if ((sender instanceof Player)) {
      this.p = ((Player)sender);
    }
    try {
      this.cfg.loadConfig();
      this.lang.InitializeLang();
      
      if (cmd.getName().equalsIgnoreCase(this.lang.mainCommand)) {
        return doArgs(args, sender);
      }
      for (String s : this.lang.aliases) {
        if (cmd.getName().equalsIgnoreCase(s))
        {
          return doArgs(args, sender);
        }
      }
      
      for (String s : this.cfg.cs.getKeys(false)) {
        if (cmd.getName().equalsIgnoreCase(s)) { HashMap<AnvilGUI.AnvilSlot, HashMap<String, HashMap<ItemStack, List<String>>>> buttons;
          HashMap<String, HashMap<ItemStack, List<String>>> preList; if ((sender instanceof Player)) {
            buttons = new HashMap<AnvilSlot, HashMap<String, HashMap<ItemStack, List<String>>>>();
            buttons = this.cfg.LoadButtonsfromConfig(s);
            preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
            preList = buttons.get(AnvilGUI.AnvilSlot.INPUT_LEFT);
            for (String perms : preList.keySet()) {
              if (!perms.equals(null)) {
                if ((this.p.hasPermission(perms)) || (this.p.hasPermission("anvilcommands.admin"))) {
                  final String passThru = s;
                  Class<?> clazz = null;
                  try {
                    clazz = Class.forName(this.plugin.clazzName);
                  } catch (ClassNotFoundException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
                    e.printStackTrace();
                    return false;
                  }
                  AnvilGUI gui = (AnvilGUI)clazz.asSubclass(clazz).getConstructor(new Class[] { Player.class, JavaPlugin.class, AnvilGUI.AnvilClickEventHandler.class }).newInstance(new Object[] { this.p, this.plugin, new AnvilGUI.AnvilClickEventHandler()
                  {
                    @EventHandler(priority=EventPriority.HIGHEST)
                    public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
                      HashMap<AnvilGUI.AnvilSlot, HashMap<String, HashMap<ItemStack, List<String>>>> buttons = new HashMap<AnvilSlot, HashMap<String, HashMap<ItemStack, List<String>>>>();
                      HashMap<String, HashMap<ItemStack, List<String>>> preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                      HashMap<ItemStack, List<String>> commandList = new HashMap<ItemStack, List<String>>();
                      buttons = AnvilCommandExecutor.this.cfg.LoadButtonsfromConfig(passThru);
                      
                      commandList = new HashMap<ItemStack, List<String>>();
                      preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                      if (event.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT) {
                        preList = buttons.get(AnvilGUI.AnvilSlot.INPUT_LEFT);
                        for (String perms : preList.keySet()) {
                          if (!perms.equals(null)) {
                            commandList = preList.get(perms);
                          }
                        }
                        AnvilCommandExecutor.this.doCommands(event, event.getName(), commandList);
                      }
                      
                      commandList = new HashMap<ItemStack, List<String>>();
                      preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                      if (event.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT) {
                        preList = buttons.get(AnvilGUI.AnvilSlot.INPUT_RIGHT);
                        for (String perms : preList.keySet()) {
                          if (!perms.equals(null)) {
                            commandList = preList.get(perms);
                          }
                        }
                        AnvilCommandExecutor.this.doCommands(event, event.getName(), commandList);
                      }
                      
                      commandList = new HashMap<ItemStack, List<String>>();
                      preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                      if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                        preList = buttons.get(AnvilGUI.AnvilSlot.OUTPUT);
                        for (String perms : preList.keySet()) {
                          if (!perms.equals(null)) {
                            commandList = preList.get(perms);
                          }
                        }
                        AnvilCommandExecutor.this.doCommands(event, event.getName(), commandList);
                      }
                      
                    }
                  } });
                  preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                  HashMap<ItemStack, List<String>> itemList = new HashMap<ItemStack, List<String>>();
                  preList = buttons.get(AnvilGUI.AnvilSlot.INPUT_LEFT);
                  for (String perm : preList.keySet()) {
                    if (!perm.equals(null)) {
                      itemList = preList.get(perm);
                      for (ItemStack input : itemList.keySet()) {
                        if (input != null) {
                          gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, input);
                        }
                      }
                    }
                  }
                  
                  itemList = new HashMap<ItemStack, List<String>>();
                  preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                  preList = buttons.get(AnvilGUI.AnvilSlot.INPUT_RIGHT);
                  for (String perm : preList.keySet()) {
                    if (!perm.equals(null)) {
                      itemList = preList.get(perm);
                      for (ItemStack input : itemList.keySet()) {
                        if (input != null) {
                          gui.setSlot(AnvilGUI.AnvilSlot.INPUT_RIGHT, input);
                        }
                      }
                    }
                  }
                  
                  itemList = new HashMap<ItemStack, List<String>>();
                  preList = new HashMap<String, HashMap<ItemStack, List<String>>>();
                  preList = buttons.get(AnvilGUI.AnvilSlot.OUTPUT);
                  for (String perm : preList.keySet()) {
                    if (!perm.equals(null)) {
                      itemList = preList.get(perm);
                      for (ItemStack input : itemList.keySet()) {
                        if (input != null) {
                          gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, input);
                        }
                      }
                    }
                  }
                  
                  gui.open();
                } else {
                  this.p.sendMessage(this.lang.PLPrefix + this.lang.NoPerms);
                }
              }
            }
          }
          return true;
        }
      }
    } catch (Exception e) {
      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
      e.printStackTrace();
    }
    return false;
  }
  
  public void doCommands(AnvilGUI.AnvilClickEvent event, String inputString, HashMap<ItemStack, List<String>> commandList)
  {
	this.GUIinput = null;
	Long delay = 10L;
    for (ItemStack input : commandList.keySet()) {
    	if (input != null) {
    		for (String com : commandList.get(input)) {
    			if (com != null) {
    				if ((com.equals("[close]")) || (com.equals("[nothing]"))) {
    					if (com == "[close]") {
    						event.setWillClose(true);
    						event.setWillDestroy(true);
    						break;
    					}
    					if (com == "[nothing]") {
    						event.setWillClose(false);
    						event.setWillDestroy(false);
    						continue;
    					}              
    				} else{
    					RunLevel level = RunLevel.PLAYER;
    					if (event.getName() != null) {
    						this.GUIinput = inputString;
    					}
    					if (com.contains("%userinput%") && !(this.GUIinput.equals(""))) {
    						com = com.replace("%userinput%", this.GUIinput);
    					}
    					if (com.contains("%player%")) {
    						com = com.replace("%player%", this.p.getName());
    					}
    					if(com.length() > 1){
    						char c = com.charAt(0);
    						if (c == '!' || c == '~') {
    							if (c =='!') {
    								level = RunLevel.OP;
    							}
    							if (c == '~') {
    								level = RunLevel.CONSOLE;
    							}
    							com = com.substring(1);
    						}
    					}
    					if (this.GUIinput != null) {
    						new CommandTask(level, plugin, p, com).runTaskLater(plugin, delay);
    						delay = delay + 20L;
    					}
    				}
    			}
    		}
    	}
   	}
  }
  
  private void showHelp(CommandSender sender)
  {
    if ((sender.hasPermission("anvilcommands.user")) || (sender.hasPermission("anvilcommands.admin")) || (!(sender instanceof Player))) {
      sender.sendMessage(ChatColor.BLUE + "------  " + ChatColor.YELLOW + this.lang.PluginName + this.lang.Help + ChatColor.BLUE + "------" + ChatColor.RESET);
      sender.sendMessage(ChatColor.AQUA + "/" + this.lang.mainCommand + " " + ChatColor.RESET + this.lang.DisplayHelp);
      sender.sendMessage(ChatColor.AQUA + "/" + this.lang.mainCommand + " help" + " " + ChatColor.RESET + this.lang.DisplayHelp);
    }
    if ((sender.hasPermission("anvilcommands.admin")) || (!(sender instanceof Player))) {
      sender.sendMessage(ChatColor.YELLOW + "--------" + ChatColor.RED + this.lang.AdminHelp + ChatColor.YELLOW + "--------" + ChatColor.RESET);
      sender.sendMessage(ChatColor.RED + "/" + this.lang.mainCommand + " version" + ChatColor.RESET + this.lang.VersionHelp);
      sender.sendMessage(ChatColor.RED + "/" + this.lang.mainCommand + " reload" + ChatColor.RESET + this.lang.Reloads);
    }
    sender.sendMessage(ChatColor.BLUE + "---------------------------------" + ChatColor.RESET);
  }
  
  private boolean doArgs(String[] args, CommandSender sender) {
    if ((args.length == 0) || (args.equals(null)))
    {
      showHelp(sender);
      return true;
    }
    if (args.length > 0) {
      String arg = args[0];
      if (arg.equalsIgnoreCase("help")) {
        showHelp(sender);
        return true;
      }
      if (arg.equalsIgnoreCase("reload")) {
        if (!sender.hasPermission(this.lang.AdminPerm)) {
          sender.sendMessage(this.lang.PLPrefix + this.lang.NoPerms);
          return true;
        }
        this.plugin.getPluginLoader().disablePlugin(this.plugin);
        this.plugin.getPluginLoader().enablePlugin(this.plugin);
        sender.sendMessage(this.lang.PLPrefix + this.lang.Reloaded);
        return true;
      }
      if (arg.equalsIgnoreCase("version")) {
        if (!(sender instanceof Player)) {
          Bukkit.getServer().getConsoleSender().sendMessage(this.lang.PLPrefix + ChatColor.LIGHT_PURPLE + this.lang.Version + ChatColor.GRAY + ": " + ChatColor.RESET + this.plugin.getDescription().getVersion());
          return true;
        }
        if (!sender.hasPermission(this.lang.AdminPerm)) {
          sender.sendMessage(this.lang.PLPrefix + this.lang.NoPerms);
          return true;
        }
        sender.sendMessage(this.lang.PLPrefix + ChatColor.LIGHT_PURPLE + this.lang.Version + ChatColor.GRAY + ": " + ChatColor.RESET + this.plugin.getDescription().getVersion());
        return true;
      }
    }
    return true;
  }
  
  public static enum RunLevel {
    PLAYER, 
    OP, 
    CONSOLE;
  }
}
