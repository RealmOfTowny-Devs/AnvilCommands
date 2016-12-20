package me.drkmatr1984.AnvilCommands;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.drkmatr1984.AnvilCommands.AnvilConfig.Anvils;
import me.drkmatr1984.AnvilCommands.tasks.CommandTask;
import net.wesjd.anvilgui.AnvilGUI;

public class AnvilCommandExecutor
  implements CommandExecutor
{
  final String variable = "%userinput%";
  final String userVariable = "%player%";
  
  AnvilCommands plugin;
  AnvilConfig cfg;
  AnvilLang lang;
  
  public AnvilCommandExecutor(AnvilCommands anvilStringCommand) {
    this.plugin = anvilStringCommand;
    this.lang = this.plugin.lang;
    this.lang.InitializeLang();
    this.cfg = this.plugin.config;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
	Player p;
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
        if (cmd.getName().equalsIgnoreCase(s)) { 
          if ((sender instanceof Player)) {
        	p = ((Player)sender);
        	HashMap<String, Anvils> buttons = new HashMap<String, Anvils>();
            buttons = this.cfg.LoadButtonsfromConfig(s);
            Anvils thisList = buttons.get(s);
            if (!thisList.getPermission().equals(null)) {
              if ((p.hasPermission(thisList.getPermission())) || (p.hasPermission("anvilcommands.admin"))) {
            	  final Player pl = p;
            	  new AnvilGUI(this.plugin, pl, thisList.getPrompt(), (player, reply) -> {
            		    if (!reply.equals(null)) {
            		    	List<String> commandList = thisList.getCommands();
            		    	doCommands(pl, reply, commandList);
            		        return null;
            		    }
            		    return "&4Canceled";
            	  });
              } else {
                p.sendMessage(this.lang.PLPrefix + this.lang.NoPerms);
              }
            }
          }
        }  
      }
      return true;
    } catch (Exception e) {
      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
      e.printStackTrace();
    }
    return false;
  }
  
  public void doCommands(Player p, String inputString, List<String> commandList)
  {
	Long delay = 10L;	
    for (String com : commandList) {
    	if (com != null) {
    		RunLevel level = RunLevel.PLAYER;
			if (com.contains("%userinput%") && !(inputString.equals(""))) {
				com = com.replace("%userinput%", inputString);
			}
			if (com.contains("%player%")) {
				com = com.replace("%player%", p.getName());
    		}
    		if(com.length() >= 1){
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
    		if (com != null) {
    			new CommandTask(level, plugin, p, com).runTaskLater(plugin, delay);
    			delay = delay + 20L;
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
