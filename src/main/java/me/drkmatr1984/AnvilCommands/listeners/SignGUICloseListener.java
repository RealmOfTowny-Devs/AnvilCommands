package me.drkmatr1984.AnvilCommands.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.drkmatr1984.AnvilCommands.AnvilCommandExecutor;
import me.drkmatr1984.AnvilCommands.AnvilCommands;
import me.drkmatr1984.AnvilCommands.events.SignInputEvent;

public class SignGUICloseListener implements Listener
{
	 AnvilCommands plugin;
	 private static HashMap<Player, List<String>> config = new HashMap<Player, List<String>>();
	 
	 public SignGUICloseListener(AnvilCommands plugin){
		 this.plugin = plugin;
	 }
	
	 @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
	 public void onSignGUIClose(SignInputEvent.SignGUICloseEvent event)
	 {
		 Player p = event.getPlayer();
		 String input = "";
		 if(event.getLines()!=null){
			 String[] lines = event.getLines();
	         for(String l : lines){
	         	input = input + l + " ";
	         }
	         List<String> list = config.get(p);
	         AnvilCommandExecutor.doCommands(p, input, list);
		 }	 
	 }

	public static HashMap<Player, List<String>> getConfig() {
		return config;
	}

	public static void setConfig(HashMap<Player, List<String>> config) {
		SignGUICloseListener.config = config;
	}
}