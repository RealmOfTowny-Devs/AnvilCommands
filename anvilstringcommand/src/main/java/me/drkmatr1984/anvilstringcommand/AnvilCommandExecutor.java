package me.drkmatr1984.anvilstringcommand;

import java.util.HashMap;
import java.util.List;

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
	AnvilStringCommand plugin;
	AnvilStringConfig cfg;
    String GUIinput = "";
    final String close = "[close]";
    final String nothing = "[nothing]";
    final String variable = "%userinput%";
    final String userVariable = "%player%";
    Player p = null;
    
	public AnvilCommandExecutor(AnvilStringCommand anvilStringCommand) {
		this.plugin = anvilStringCommand;
	}
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		try {
			// AnvilGUI Commands Registered by Config
			cfg = new AnvilStringConfig(plugin);
			cfg.loadConfig();
			for(String s : cfg.cs.getKeys(false)){
				if (cmd.getName().equalsIgnoreCase(s)) {
					if(sender instanceof Player){
						p = (Player)sender;
			    		HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>> buttons = new HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>>();
				    	buttons = cfg.LoadButtonsfromConfig(s);
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
			    		HashMap<String,HashMap<ItemStack, List<String>>> preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
			    		HashMap<ItemStack, List<String>> itemList = new HashMap<ItemStack, List<String>>();
			    		preList = buttons.get(AnvilSlot.INPUT_LEFT);
			    		for(String perms : preList.keySet()){
			    			if(!perms.equals(null)){
			    				itemList = preList.get(perms);
			    				for(ItemStack input : itemList.keySet()){
						    		if(input!=null){
						    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, input);
						    		}
						    	}
			    			}
			    		}	
				    	
			    		itemList = new HashMap<ItemStack, List<String>>();
			    		preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
				    	preList = buttons.get(AnvilSlot.INPUT_RIGHT);
				    	for(String perms : preList.keySet()){
			    			if(!perms.equals(null)){
			    				itemList = preList.get(perms);
			    				for(ItemStack input : itemList.keySet()){
						    		if(input!=null){
						    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_RIGHT, input);
						    		}
						    	}
			    			}
			    		}
				    	
				    	itemList = new HashMap<ItemStack, List<String>>();
			    		preList = new HashMap<String,HashMap<ItemStack, List<String>>>();
				    	preList = buttons.get(AnvilSlot.OUTPUT);
				    	for(String perms : preList.keySet()){
			    			if(!perms.equals(null)){
			    				itemList = preList.get(perms);
			    				for(ItemStack input : itemList.keySet()){
						    		if(input!=null){
						    			gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, input);
						    		}
						    	}
			    			}
			    		}
			    	    gui.open();
			    	  } 
			    	  return true;
			      }
				}
			}catch (Exception e) {
			      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
			      e.printStackTrace();
			}      
			return false;
	}
		
	public void doCommands(AnvilClickEvent event, HashMap<ItemStack, List<String>> commandList){
		for(ItemStack input : commandList.keySet()){
			if(input != null){
    			for(String com : commandList.get(input)){
    				if(com!=null){
						if(com.equals(close) || com.equals(nothing)){
							if(com == close){
								event.setWillClose(true);
								event.setWillDestroy(true);
							}
							if(com == nothing){
								event.setWillClose(false);
								event.setWillDestroy(false);
							}
						}
						else{
							RunLevel level = RunLevel.PLAYER;
							GUIinput = event.getName();
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
	
	public enum RunLevel{
		PLAYER,
        OP,
        CONSOLE;
	}
}