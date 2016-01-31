package me.drkmatr1984.anvilstringcommand;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Field;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

//import org.bukkit.plugin.PluginManager;

public class AnvilStringCommand extends JavaPlugin
{
    public static AnvilStringCommand plugin;
    public boolean anvilPatch = false;
    AnvilStringConfig config;
    private Logger log = getLogger();
    //private PluginManager pm = getServer().getPluginManager(); //Used to register Events/Listeners
    private static CommandMap cmap;
    public String version;
    public String clazzName;
    public String patchName;
  
    public void onEnable()
    {
    	//creates a plugin instance for easy access to plugin
    	plugin = this;
    	version = this.getNmsVersion().replace("_", "").toLowerCase();
    	RegisterCommands();   	
    	if(!RegisterAnvilGUI()){
    		this.log.log(Level.WARNING, "Plugin could not be loaded, version {" + version + "} is not supported!");
    	}
    	anvilPatch = anvilPatch();	
    	this.log.info("AnvilStringCommand enabled!");
    }
  
    public void onDisable()
    {
    	unRegisterCommands();
	  	plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
  
    public CommandMap getCommandMap(){
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
  
    private void RegisterCommands(){
    	//These next lines loop thru the config and get a list of all the commands registered there
    	config = new AnvilStringConfig(this);
    	config.saveDefaultConfig();
    	config.loadConfig();
    	String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
    	try {
    		Class<?> clazz = Class.forName(cbukkit);
			try{
				final Field f = clazz.getDeclaredField("commandMap");
				f.setAccessible(true);
				cmap = (CommandMap)f.get(Bukkit.getServer());
				CCommand acs = new CCommand("anvilcommands");
				cmap.register("", acs);
				acs.setExecutor(new AnvilCommandExecutor(this));
				this.log.info("Command " + acs.getName() + " Registered!");
				if(config.cs != null){
					for(String s : config.cs.getKeys(false)){
						if(s!=null){
							CCommand cmd = new CCommand(s);
							cmap.register("", cmd);
							cmd.setExecutor(new AnvilCommandExecutor(this));
							this.log.info("Command " + s + " Registered!");
						}    
					}
		  	   	}else{
		  	   		this.log.info("Can't retrieve the Command List");
		  	   	}
			} catch (Exception e){
				e.printStackTrace();
			}
    	} catch (ClassNotFoundException e) {
    		this.log.log(Level.WARNING, "Plugin could not be loaded, is this even Spigot or CraftBukkit?");
			this.setEnabled(false);	
		}
    }
  
    private void unRegisterCommands()
    {
    	//These next lines loop thru the config and get a list of all the commands registered there
    	config = new AnvilStringConfig(this);
    	config.loadConfig();
    	String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
    	try {
    		Class<?> clazz = Class.forName(cbukkit);
    		try{
    			final Field f = clazz.getDeclaredField("commandMap");
    			f.setAccessible(true);
    			cmap = (CommandMap)f.get(Bukkit.getServer());
    			CCommand acs = new CCommand("anvilcommands");
				acs.unregister(cmap);
				this.log.info("Command " + acs.getName() + " Unregistered!");
				//Not sure if these are unregistering properly or not. Needs Debugging.
    			if(config.cs != null){
    				for(String s : config.cs.getKeys(false)){
    					if(s!=null){
    						CCommand cmd = new CCommand(s);
    						if(cmd.isRegistered())
    						{
    							cmd.unregister(cmap);
    							this.log.info("Command " + s + " Unregistered!");
    						}
    					}    
    				}
    			}else{
    				this.log.info("Can't retrieve the Command List");
    			}
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    	} catch (ClassNotFoundException e) {
    		this.log.log(Level.WARNING, "Plugin could not unload commands, is this even Spigot or CraftBukkit?");
    	}
    }
  
    private boolean RegisterAnvilGUI()
    {
    	clazzName = this.getClass().getPackage().getName() + "." + version + ".SAnvilGUI";
    	try {
    		Class<?> clazz = Class.forName(clazzName);
    		if (AnvilGUI.class.isAssignableFrom(clazz)) {
    			return true;
    		}
    		this.getLogger().log(Level.WARNING, "Plugin could not be loaded, version {" + version + "} is not supported yet!");
    		this.setEnabled(false);
    		return false;
    	} catch (ClassNotFoundException e) {
    		this.getLogger().log(Level.WARNING, "Plugin could not be loaded, version {" + version + "} is not supported yet!");
    		this.setEnabled(false);
    		return false;
    	}	  
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
    		patchName = this.getClass().getPackage().getName() + "." + version + ".SAnvilPatcher";
    		try {
    			Class<?> clazz = Class.forName(patchName);
    			if (AnvilPatcher.class.isAssignableFrom(clazz)) {
    				System.out.println("[AnvilStringCommand] AnvilPatch not found. Using built-in code to handle colors in GUI!");
    				return false;
    			}
    			this.setEnabled(false);
    			return false;
    		} catch (ClassNotFoundException e) {
    			this.setEnabled(false);
    			return false;
    		}		  
    	}	  
    	return true;
    }
}