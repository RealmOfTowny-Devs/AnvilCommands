package me.drkmatr1984.anvilstringcommand;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.drkmatr1984.anvilstringcommand.AnvilGUI.AnvilClickEvent;
import me.drkmatr1984.anvilstringcommand.AnvilGUI.AnvilClickEventHandler;
import me.drkmatr1984.anvilstringcommand.AnvilGUI.AnvilSlot;

public class AnvilCommandExecutor implements org.bukkit.command.CommandExecutor
{   
	
    final String close = "[close]";
    final String nothing = "[nothing]";
    final String variable = "%userinput%";
    final String userVariable = "%player%";
    AnvilStringCommand plugin;
	AnvilStringConfig cfg;
    String GUIinput = "";
    String noPerm = ChatColor.RED + "You do not have permission to open this GUI";
    Player p = null;
    String adminPerm = "anvilcommands.admin";
    String plugPrefix = ChatColor.GRAY + "["+ ChatColor.BLUE + "AnvilCommands" + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
    
	public AnvilCommandExecutor(AnvilStringCommand anvilStringCommand) {
		this.plugin = anvilStringCommand;
	}
	
	@Override
	public boolean onCommand(CommandSender sender1, Command cmd, String label, String[] args) {
		final CommandSender sender = sender1;
		if(sender instanceof Player){
			p = (Player)sender;
		}
		try {
			cfg = new AnvilStringConfig(plugin);
			cfg.loadConfig();
			
			//Main Plugin Commands and Args
			if (cmd.getName().equalsIgnoreCase("anvilcommands")) {
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
			        	  if (!sender.hasPermission(adminPerm)) {
			        		  sender.sendMessage(plugPrefix + noPerm);
				              return true;
				          }
				          this.plugin.getPluginLoader().disablePlugin(this.plugin);
				          this.plugin.getPluginLoader().enablePlugin(plugin);
				          sender.sendMessage(plugPrefix + ChatColor.BLUE + "AnvilCommands has been Reloaded!");
				          return true;
				      }
			          if (arg.equalsIgnoreCase("version")) {
			        	  if (!(sender instanceof Player)) {
				              Bukkit.getServer().getConsoleSender().sendMessage(plugPrefix + ChatColor.LIGHT_PURPLE + "Version" + ChatColor.GRAY + ": " + ChatColor.RESET + plugin.getDescription().getVersion());
				              return true;
				          }
			        	  if (!sender.hasPermission(adminPerm)) {
			        		  sender.sendMessage(plugPrefix + noPerm);
				              return true;
			        	  }
				          sender.sendMessage(plugPrefix + ChatColor.LIGHT_PURPLE + "Version" + ChatColor.GRAY + ": " + ChatColor.RESET + plugin.getDescription().getVersion());
				          return true;
			          }
				}
			}else{
				// AnvilGUI Commands Registered by Config
				for(String s : cfg.cs.getKeys(false)){
					if (cmd.getName().equalsIgnoreCase(s)) {
						if(sender instanceof Player){
				    		HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>> buttons = new HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>>();
					    	buttons = cfg.LoadButtonsfromConfig(s);
					    	HashMap<String,HashMap<ItemStack, List<String>>> preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
					    	preList = buttons.get(AnvilSlot.INPUT_LEFT);
				    		for(String perms : preList.keySet()){
				    			if(!perms.equals(null)){
				    				if(p.hasPermission(perms) || p.hasPermission("anvilcommands.admin")){
								    	final String passThru = s;
							    		Class<?> clazz = null;
									    try{
									    	clazz = Class.forName(plugin.clazzName);
									    } catch (ClassNotFoundException e) {
									    	sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
										    e.printStackTrace();
									    	return false;
									    }								    
							    		AnvilGUI gui = (AnvilGUI)clazz.asSubclass(clazz).getConstructor(Player.class, JavaPlugin.class, AnvilClickEventHandler.class).newInstance(p, plugin, new AnvilGUI.AnvilClickEventHandler(){
							    		@Override
							    		@EventHandler(priority=org.bukkit.event.EventPriority.HIGHEST)
										public void onAnvilClick(AnvilClickEvent event) {
											HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>> buttons = new HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>>();
											HashMap<String,HashMap<ItemStack, List<String>>> preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
											HashMap<ItemStack, List<String>> commandList = new HashMap<ItemStack, List<String>>();
											buttons = cfg.LoadButtonsfromConfig(passThru);
												//Load Left_Input Slot from Config
												commandList = new HashMap<ItemStack, List<String>>();
												preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
												if(event.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT){
										    		preList = buttons.get(AnvilSlot.INPUT_LEFT);
										    		for(String perms : preList.keySet()){
										    			if(!perms.equals(null)){
										    				commandList = preList.get(perms);						    						
										    			}
										    		}
										    		doCommands(event, commandList);
												}
												//Load Right_Input Slot from Config
												commandList = new HashMap<ItemStack, List<String>>();
												preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
										    	if(event.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT){
										    		preList = buttons.get(AnvilSlot.INPUT_RIGHT);
										    		for(String perms : preList.keySet()){
										    			if(!perms.equals(null)){
										    				commandList = preList.get(perms);						    						
										    			}
										    		}
										    		doCommands(event, commandList);
												}
										    	//Load Output Slot from Config
										    	commandList = new HashMap<ItemStack, List<String>>();
												preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
												if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
													preList = buttons.get(AnvilSlot.OUTPUT);
										    		for(String perms : preList.keySet()){
										    			if(!perms.equals(null)){
										    				commandList = preList.get(perms);						    						
										    			}
										    		}
													doCommands(event, commandList);
												}
											}
										});
							    		//Set Left_Input Slot
							    		preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
							    		HashMap<ItemStack, List<String>> itemList = new HashMap<ItemStack, List<String>>();
							    		preList = buttons.get(AnvilSlot.INPUT_LEFT);
							    		for(String perm : preList.keySet()){
							    			if(!perm.equals(null)){
							    				itemList = preList.get(perm);
							    				for(ItemStack input : itemList.keySet()){
										    		if(input!=null){
										    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, input);
										    		}
										    	}
							    			}
							    		}	
								    	//Set Right_Input Slot
							    		itemList = new HashMap<ItemStack, List<String>>();
							    		preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
								    	preList = buttons.get(AnvilSlot.INPUT_RIGHT);
								    	for(String perm : preList.keySet()){
							    			if(!perm.equals(null)){
							    				itemList = preList.get(perm);
							    				for(ItemStack input : itemList.keySet()){
										    		if(input!=null){
										    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_RIGHT, input);
										    		}
										    	}
							    			}
							    		}
								    	//Set Output_Slot
								    	itemList = new HashMap<ItemStack, List<String>>();
							    		preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
								    	preList = buttons.get(AnvilSlot.OUTPUT);
								    	for(String perm : preList.keySet()){
							    			if(!perm.equals(null)){
							    				itemList = preList.get(perm);
							    				for(ItemStack input : itemList.keySet()){
										    		if(input!=null){
										    			gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, input);
										    		}
										    	}
							    			}
							    		}
								    	//Open the GUI We just assembled
							    	    gui.open();
						    		}else{
						    			p.sendMessage(plugPrefix + noPerm);
						    		}						    						
				    			}
				    		}
				    	  } 
				    	  return true;
				      }
					}
				}
			}catch (Exception e) {
			      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
			      e.printStackTrace();
			}      
			return false;
	}
		
	public void doCommands(AnvilClickEvent event, HashMap<ItemStack, List<String>> commandList){
		//Loop thru buttons and do the commands assigned to them when clicked
		for(ItemStack input : commandList.keySet()){
			if(input != null){
    			for(String com : commandList.get(input)){
    				if(com!=null){
						if(com.equals(close) || com.equals(nothing)){
							if(com == close){
								event.setWillClose(true);
								event.setWillDestroy(true);
								continue;
							}
							if(com == nothing){
								event.setWillClose(false);
								event.setWillDestroy(false);
								continue;
							}
						}
						else{
							//Do commands with RunLevels
							RunLevel level = RunLevel.PLAYER;
							if(event.getName()!=null){
								GUIinput = event.getName();
							}else{
								event.setWillClose(false);
								event.setWillDestroy(false);
								break;
							}
							if(com.contains(variable)){
								com = com.replace(variable, GUIinput);
							}
							if(com.contains(userVariable)){
								com = com.replace(userVariable, p.getName());
							}
							if((com.substring(0, 1)).equals("!") || (com.substring(0, 1)).equals("~")){
								if((com.substring(0, 1)).equals("!")){
									level = RunLevel.OP;
								}
								if((com.substring(0, 1)).equals("~")){
									level = RunLevel.CONSOLE;
								}
								com = com.substring(1);
							}
							if(event.getName()!=null){
								switch(level){
									case PLAYER:
										p.performCommand(com);
										break;
									case OP:
										if(!p.isOp()){
											p.setOp(true);
											p.performCommand(com);
											p.setOp(false);
										}else{
											p.performCommand(com);
										}
										break;
									case CONSOLE:
	                                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), com);
	                                    break;
								}
							}
						}						
					}
    			}
			}
		}
	}
	
	public void showHelp(CommandSender sender){
		if ((sender.hasPermission("anvilcommands.user") || sender.hasPermission("anvilcommands.admin")) && (sender instanceof Player)) {
		sender.sendMessage(ChatColor.BLUE + "------  " + ChatColor.YELLOW + "AnvilCommands" + " Help  " + ChatColor.BLUE + "------" + ChatColor.RESET);
        sender.sendMessage(ChatColor.AQUA + "/" + "AnvilCommands" + " " + ChatColor.RESET + "- Displays this Help");
        sender.sendMessage(ChatColor.AQUA + "/" + "AnvilCommands help" + " " + ChatColor.RESET + "- Also Displays this Help");
		}
		if (sender.hasPermission("anvilcommands.admin") || !(sender instanceof Player)) {
      	  sender.sendMessage(ChatColor.YELLOW + "--------" + ChatColor.RED + " Admin Help " + ChatColor.YELLOW + "--------" + ChatColor.RESET);
          sender.sendMessage(ChatColor.RED + "/" + "AnvilCommands" + " version" + ChatColor.RESET + " - Shows the Plugin Version Number");
          sender.sendMessage(ChatColor.RED + "/" + "AnvilCommands" + " reload" + ChatColor.RESET + " - Reloads the Plugin Config");
        }
        sender.sendMessage(ChatColor.BLUE + "---------------------------------" + ChatColor.RESET);
	}
	
	public enum RunLevel{
		PLAYER,
        OP,
        CONSOLE;
	}
}