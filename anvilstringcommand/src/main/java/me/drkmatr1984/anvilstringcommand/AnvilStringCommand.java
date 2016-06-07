package me.drkmatr1984.anvilstringcommand;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilStringCommand
  extends JavaPlugin
{
  public static AnvilStringCommand plugin;
  public boolean anvilPatch = false;
  public AnvilStringConfig config;
  public AnvilLang lang;
  private Logger log = getLogger();
  
  private static CommandMap cmap;
  
  public String version;
  public String clazzName;
  
  public void onEnable()
  {
    plugin = this;
    this.version = getNmsVersion().replace("_", "").toLowerCase();
    this.config = new AnvilStringConfig(this);
    this.lang = new AnvilLang();
    this.lang.InitializeLang();
    this.config.saveDefaultConfig();
    this.config.loadConfig();
    RegisterCommands();
    if (!RegisterAnvilGUI()) {
      this.log.log(Level.WARNING, "Plugin could not be loaded, version {" + this.version + "} is not supported!");
    }
    this.anvilPatch = anvilPatch();
    this.log.info("AnvilCommands enabled!");
  }
  
  public void onDisable()
  {
    this.config.loadConfig();
    unRegisterCommands();
    plugin.getServer().getPluginManager().disablePlugin(plugin);
  }
  
  public CommandMap getCommandMap() {
    return cmap;
  }
  
  public class CCommand extends Command{
    	private CommandExecutor exe = null;
    	protected CCommand(String name) {
    		super(name);
    	}
    	public boolean execute(CommandSender sender, String commandLabel,String[] args) {
    		if(exe != null){
    			exe.onCommand(sender, this, commandLabel,args);
    		}
    		return false;
    	}
    	public void setExecutor(CommandExecutor exe){
    		this.exe = exe;
    	}
  }
  

  private void RegisterCommands()
  {
    String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
    try {
      Class<?> clazz = Class.forName(cbukkit);
      try {
        Field f = clazz.getDeclaredField("commandMap");
        f.setAccessible(true);
        cmap = (CommandMap)f.get(Bukkit.getServer());
        if (!this.lang.mainCommand.equals(null))
        {
          CCommand acs = new CCommand(this.lang.mainCommand);
          cmap.register("anvilcommands", acs);
          acs.setExecutor(new AnvilCommandExecutor(this));
          this.log.info("Command " + this.lang.mainCommand + " Registered!"); }
        Iterator<String> l;
        if (!this.lang.aliases.isEmpty())
        {
          for (l = this.lang.aliases.iterator(); l.hasNext();)
          {
            String s = (String)l.next();
            if ((!s.equals(null)) || (!s.equals("")))
            {
              CCommand acs1 = new CCommand(s);
              cmap.register("anvilcommands", acs1);
              acs1.setExecutor(new AnvilCommandExecutor(this));
              this.log.info("Command " + s + " Registered!");
            }
          }
        }
        if (this.config.cs != null) {
          for (String s : this.config.cs.getKeys(false)) {
            if (s != null) {
              CCommand cmd = new CCommand(s);
              cmap.register("anvilcommands", cmd);
              cmd.setExecutor(new AnvilCommandExecutor(this));
              this.log.info("Command " + s + " Registered!");
            }
          }
        } else {
          this.log.info("AnvilCommands can't retrieve the Command List");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (ClassNotFoundException e) {
      this.log.log(Level.WARNING, "AnvilCommands could not be loaded, is this even Spigot or CraftBukkit?");
      setEnabled(false);
    }
  }
  

  private void unRegisterCommands()
  {
    String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
    try {
      Class<?> clazz = Class.forName(cbukkit);
      try {
        Field f = clazz.getDeclaredField("commandMap");
        f.setAccessible(true);
        cmap = (CommandMap)f.get(Bukkit.getServer());
        if (!this.lang.mainCommand.equals(null))
        {
          CCommand acs = new CCommand(this.lang.mainCommand);
          acs.unregister(cmap);
          this.log.info("Command " + this.lang.mainCommand + " Unregistered!"); }
        Iterator<String> l;
        if (!this.lang.aliases.isEmpty())
        {
          for (l = this.lang.aliases.iterator(); l.hasNext();)
          {
            String s = (String)l.next();
            if ((!s.equals(null)) || (!s.equals("")))
            {
              CCommand acs = new CCommand(s);
              acs.unregister(cmap);
              this.log.info("Command " + s + " Unregistered!");
            }
          }
        }
        
        if (this.config.cs != null) {
          for (String s : this.config.cs.getKeys(false)) {
            if (s != null) {
              CCommand cmd = new CCommand(s);
              cmd.unregister(cmap);
              this.log.info("Command " + s + " Unregistered!");
            }
          }
        } else {
          this.log.info("AnvilCommands can't retrieve the Command List");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (ClassNotFoundException e) {
      this.log.log(Level.WARNING, "AnvilCommands could not unload commands, is this even Spigot or CraftBukkit?");
    }
  }
  
  private boolean RegisterAnvilGUI()
  {
    this.clazzName = (getClass().getPackage().getName() + "." + this.version + ".SAnvilGUI");
    try {
      Class<?> clazz = Class.forName(this.clazzName);
      if (AnvilGUI.class.isAssignableFrom(clazz)) {
        return true;
      }
      getLogger().log(Level.WARNING, "AnvilCommands could not be loaded, version {" + this.version + "} is not supported yet!");
      setEnabled(false);
      return false;
    } catch (ClassNotFoundException e) {
      getLogger().log(Level.WARNING, "AnvilCommands could not be loaded, version {" + this.version + "} is not supported yet!");
      setEnabled(false); }
    return false;
  }
  

  public static AnvilStringCommand getInstance()
  {
    return plugin;
  }
  
  private String getNmsVersion()
  {
    return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
  }
  
  private boolean anvilPatch()
  {
    if ((plugin.getServer().getPluginManager().getPlugin("AnvilPatch") == null) || (!plugin.getServer().getPluginManager().isPluginEnabled("AnvilPatch"))) {
      System.out.println("[AnvilCommands] AnvilPatch not found. Using built-in code to handle colors in GUI!");
      return false;
    }
    return true;
  }
}
