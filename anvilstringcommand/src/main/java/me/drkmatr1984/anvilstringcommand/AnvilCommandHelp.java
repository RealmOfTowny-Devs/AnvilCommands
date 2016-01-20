package me.drkmatr1984.anvilstringcommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnvilCommandHelp implements org.bukkit.command.CommandExecutor
{   
	AnvilStringCommand plugin = AnvilStringCommand.getInstance();
    Player p = null;
    
		public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
			try {
			      if (cmd.getName().equalsIgnoreCase("help")) {
			    	  if(sender instanceof Player){
			    		  
			    	  } 
			    	  return true;
			      }
			}catch (Exception e) {
			      sender.sendMessage(ChatColor.DARK_RED + "An Error has Occured");
			}      
			return false;
	}
}