package me.drkmatr1984.AnvilCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

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
		Types types = Types.ANVIL;
		String prompts = "";
		List<String> commands = new ArrayList<String>();
		HashMap<String, Anvils> buttonSlots = new HashMap<String, Anvils>();
		for(String s : cs.getKeys(false)){
		    if(s.equals(command)){
		    	//permission
		    	String perm = "config.commands." + s + ".permission";
		    	if(f.getString(perm)!=null)
		    		permission = f.getString(perm);
		    	//Type of GUI (Anvil, Sign, CommandBlock)
		    	String type = "config.commands." + s + ".type";
		    	if(f.getString(type)!=null){
		    		types = Types.valueOf(f.getString(type));
		    	}
		    	//prompt
		    	String prompt = "config.commands." + s + ".prompt";
		    	if(f.getString(prompt)!=null){
		    		prompts = f.getString(prompt);
		    	}
		    	//Result
		    	String result = "config.commands." + s + ".result";
		    	if(f.getStringList(result + ".command")!=null)
		    		commands = f.getStringList(result + ".command");
		    	Anvils buttonInfo = new Anvils(permission, types, prompts, commands); 
		    	buttonSlots.put(s, buttonInfo);
		    	return buttonSlots;
		    }
		}
		return null;
	}
	
	public class Anvils {
		private String permission;
		private Types type;
		private String prompt;
		private List<String> commands;
		
		public Anvils(String permission, Types type, String prompt, List<String> commands){
			super();
			this.permission = permission;
			this.type = type;
			this.prompt = prompt;
			this.commands = commands;
		}
		
		public String getPermission(){
			return permission;
		}
		
		public void setPermission(String permission){
			this.permission = permission;
		}
		
		public Types getType(){
			return type;
		}
		
		public void setType(Types type){
			this.type = type;
		}
		
		public String getPrompt(){
			return prompt;
		}
		
		public void setPrompt(String prompt){
			this.prompt = prompt;
		}
		
		public List<String> getCommands(){
			return commands;
		}
		
		public void setCommands(List<String> commands){
			this.commands = commands;
		}
		
	}
	
	public static enum Types {
		ANVIL,
		SIGN;
	}
}