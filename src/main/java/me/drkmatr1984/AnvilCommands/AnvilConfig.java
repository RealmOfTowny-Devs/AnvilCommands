package me.drkmatr1984.AnvilCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

public class AnvilConfig{
	
	AnvilCommands plugin;
	
	File file;
	FileConfiguration f;
	ConfigurationSection cs;
	public AnvilConfig(AnvilCommands anvilStringCommand) {
		this.plugin = anvilStringCommand; 
	}

	public void saveDefaultConfig(){
		file = new File(this.plugin.getDataFolder(), "config.yml");
		if(!file.exists()) {
		       this.plugin.saveResource("config.yml", true);
		}    
	}
	
	public void loadConfig(){
		file = new File(this.plugin.getDataFolder(), "config.yml");
		f = YamlConfiguration.loadConfiguration(file);
		cs = f.getConfigurationSection("config.commands");
	}
	
	public HashMap<String, Anvils> LoadButtonsfromConfig(String command) {
		file = new File(this.plugin.getDataFolder(), "config.yml");
		f = YamlConfiguration.loadConfiguration(file);		
		cs = f.getConfigurationSection("config.commands");
		String permission = "anvilcommands.default";
		String displayname = "&6Apple";
		String prompts = "";
		List<String> commands = new ArrayList<String>();
		HashMap<String, Anvils> buttonSlots = new HashMap<String, Anvils>();
		for(String s : cs.getKeys(false)){
		    if(s.equals(command)){
		    	//permission
		    	String perm = "config.commands." + s + ".permission";
		    	if(f.getString(perm)!=null)
		    		permission = f.getString(perm);
		    	//prompt
		    	String prompt = "config.commands." + s + ".prompt";
		    	if(f.getString(prompt)!=null){
		    		prompts = f.getString(prompt);
		    	}
		    	//Result
		    	String result = "config.commands." + s + ".result";
		    	if(f.getStringList(result + ".command")!=null)
		    		commands = f.getStringList(result + ".command");
		    	Anvils buttonInfo = new Anvils(permission, prompts, displayname, commands); 
		    	buttonSlots.put(s, buttonInfo);
		    	return buttonSlots;
		    }
		}
		return null;
	}
	
	public String formatColor(String words)
	{
		String temp = "";
		if(!(words.equals(null)) && (!words.equals(""))){
		    if(words.contains("&")){
			temp = ChatColor.translateAlternateColorCodes('&', words);
			return temp;
		    }
		}else{
			return "";
		}
		return words;	
	}
	
	public ItemStack assembleButton(Material mat, short dv, String name, List<String> lore){
		ItemStack item = new ItemStack(mat, 1, dv);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public class Anvils {
		private String permission;
		private String prompt;
		private String displayName;
		private List<String> commands;
		
		public Anvils(String permission, String prompt, String displayName, List<String> commands){
			super();
			this.permission = permission;
			this.prompt = prompt;
			this.displayName = displayName;
			this.commands = commands;
		}
		
		public String getPermission(){
			return permission;
		}
		
		public void setPermission(String permission){
			this.permission = permission;
		}
		
		public String getPrompt(){
			return prompt;
		}
		
		public void setPrompt(String prompt){
			this.prompt = prompt;
		}
		
		public String getdisplayName(){
			return displayName;
		}
		
		public void setdisplayName(String displayName){
			this.displayName = displayName;
		}
		
		public List<String> getCommands(){
			return commands;
		}
		
		public void setCommands(List<String> commands){
			this.commands = commands;
		}
		
	}
}