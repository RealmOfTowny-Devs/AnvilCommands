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
    String close = "[close]";
    String nothing = "[nothing]";
    String variable = "%userinput%";
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
			    		  HashMap<AnvilSlot, HashMap<ItemStack, List<String>>> buttons = new HashMap<AnvilSlot, HashMap<ItemStack, List<String>>>();
				    	  buttons = cfg.LoadButtonsfromConfig(s);
			    		  final String passThru = s;
			    		  Class<?> clazz = Class.forName(plugin.clazzName);
			    		  AnvilGUI gui = (AnvilGUI)clazz.asSubclass(clazz).getConstructor(Player.class, JavaPlugin.class, AnvilClickEventHandler.class).newInstance(p, plugin, new AnvilGUI.AnvilClickEventHandler(){
							@EventHandler(priority=org.bukkit.event.EventPriority.HIGHEST)
							public void onAnvilClick(AnvilClickEvent event) {
								HashMap<AnvilSlot, HashMap<ItemStack, List<String>>> buttons = new HashMap<AnvilSlot, HashMap<ItemStack, List<String>>>();
								HashMap<ItemStack, List<String>> commandList = new HashMap<ItemStack, List<String>>();
								buttons = cfg.LoadButtonsfromConfig(passThru);
						    	if(event.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT){						    		
						    		commandList = buttons.get(AnvilSlot.INPUT_LEFT);
						    		doCommands(event, commandList);
								}
						    	if(event.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT){
						    		commandList = buttons.get(AnvilSlot.INPUT_RIGHT);
						    		doCommands(event, commandList);
								}
						    	
								if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){								
									commandList = buttons.get(AnvilSlot.OUTPUT);
									doCommands(event, commandList);
								}
							}
						});
			    		HashMap<ItemStack, List<String>> itemList = new HashMap<ItemStack, List<String>>();
			    		itemList = buttons.get(AnvilSlot.INPUT_LEFT);
				    	for(ItemStack input : itemList.keySet()){
				    		if(input!=null){
				    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, input);
				    		}
				    	}
				    	itemList = buttons.get(AnvilSlot.INPUT_RIGHT);
				    	for(ItemStack input : itemList.keySet()){
				    		if(input!=null){
				    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_RIGHT, input);
				    		}
				    	}
				    	itemList = buttons.get(AnvilSlot.OUTPUT);
				    	for(ItemStack input : itemList.keySet()){
				    		if(input!=null){
				    			gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, input);
				    		}
				    	}
			    	    gui.open();
			    	  } 
			    	  return true;
			      }
				}
			}catch (Exception e) {
			      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
			      plugin.getLogger().info(e.getStackTrace().toString());
			      e.printStackTrace();
			}      
			return false;
	}
		
	public void doCommands(AnvilClickEvent event, HashMap<ItemStack, List<String>> commandList){
		for(ItemStack input : commandList.keySet()){
			if(input != null){
    			for(String com : commandList.get(input)){
    				if(com!=null){
						if(com == close || com == nothing){
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
							GUIinput = event.getName();
							if(com.contains(variable)){
								String run = com.replace(variable, GUIinput);
								p.performCommand(run);
							}else{
								p.performCommand(com);
							}
							event.setWillClose(true);
							event.setWillDestroy(true);
						}
    				}
				}
			}
		}
	}
}