package me.drkmatr1984.anvilstringcommand;

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

import me.drkmatr1984.anvilstringcommand.AnvilGUI.AnvilSlot;

public class AnvilStringConfig{
	
	AnvilStringCommand plugin;
	
	File file;
	FileConfiguration f;
	ConfigurationSection cs;
	public AnvilStringConfig(AnvilStringCommand anvilStringCommand) {
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
	
	public HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>> LoadButtonsfromConfig(String commands) {
		file = new File(this.plugin.getDataFolder(), "config.yml");
		f = YamlConfiguration.loadConfiguration(file);		
		cs = f.getConfigurationSection("config.commands");
		String permission = "anvilcommands.default";
		String material = "APPLE";
		short datavalue = -1;
		String displayname = "&6Apple";
		List<String> lore = new ArrayList<String>();
		List<String> command = new ArrayList<String>();
		HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>> buttonSlots = new HashMap<AnvilSlot, HashMap<String,HashMap<ItemStack, List<String>>>>();
		HashMap<String,HashMap<ItemStack, List<String>>> permSlots = new HashMap<String,HashMap<ItemStack, List<String>>>();
		HashMap<ItemStack, List<String>> buttons = new HashMap<ItemStack, List<String>>();
		for(String s : cs.getKeys(false)){
		    if(s.equals(commands)){
		    	//permission
		    	String perm = "config.commands." + s + ".permission";
		    	if(f.getString(perm)!=null)
		    		permission = f.getString(perm);
		    	//Slot1
		    	String slot1 = "config.commands." + s + ".slot1";
		    	if(f.getString(slot1 + ".material")!=null)
		    		material = f.getString(slot1 + ".material");
		    	if((short)f.getInt(slot1 + ".datavalue") != -1)
		    		datavalue = (short) f.getInt(slot1 + ".datavalue");
		    	if(f.getString(slot1 + ".displayname")!=null){
		    		displayname = formatColor(f.getString(slot1 + ".displayname"));
		    		displayname = ChatColor.stripColor(displayname);
		    	}
		    	if(f.getStringList(slot1 + ".lore")!=null){
		    		List<String> loretemp = new ArrayList<String>();
		    		lore = f.getStringList(slot1 + ".lore");
		    		for(String l : lore){
		    			loretemp.add(formatColor(l));
		    		}
		    		lore = loretemp;
		    	}
		    	if(f.getStringList(slot1 + ".command")!=null)
		    		command = f.getStringList(slot1 + ".command");
		    	buttons = new HashMap<ItemStack, List<String>>();
		    	buttons.put(assembleButton(Material.getMaterial(material), datavalue, displayname, lore), command);
		    	permSlots = new HashMap<String,HashMap<ItemStack, List<String>>>();
		    	permSlots.put(permission, buttons);
		    	buttonSlots.put(AnvilSlot.INPUT_LEFT, permSlots);
		    	//Slot2
		    	String slot2 = "config.commands." + s + ".slot2";
		    	if(f.getString(slot2 + ".material")!=null)
		    		material = f.getString(slot2 + ".material");
		    	if((short)f.getInt(slot2 + ".datavalue") != -1)
		    		datavalue = (short) f.getInt(slot2 + ".datavalue");
		    	if(f.getString(slot2 + ".displayname")!=null)
		    		displayname = formatColor(f.getString(slot2 + ".displayname"));
		    	if(f.getStringList(slot2 + ".lore")!=null){
		    		List<String> loretemp = new ArrayList<String>();
		    		lore = f.getStringList(slot2 + ".lore");
		    		for(String l : lore){
		    			loretemp.add(formatColor(l));
		    		}
		    		lore = loretemp;
		    	}
		    	if(f.getStringList(slot2 + ".command")!=null)
		    		command = f.getStringList(slot2 + ".command");
		    	buttons = new HashMap<ItemStack, List<String>>();
		    	buttons.put(assembleButton(Material.getMaterial(material), datavalue, displayname, lore), command);
		    	permSlots = new HashMap<String,HashMap<ItemStack, List<String>>>();
		    	permSlots.put(permission, buttons);
		    	buttonSlots.put(AnvilSlot.INPUT_RIGHT, permSlots);
		    	//Result
		    	String result = "config.commands." + s + ".result";
		    	ItemStack stack = new ItemStack(Material.NAME_TAG, 1, (short)0);
		    	if(f.getStringList(result + ".command")!=null)
		    		command = f.getStringList(result + ".command");
		    	buttons = new HashMap<ItemStack, List<String>>();
		    	buttons.put(stack, command);
		    	permSlots = new HashMap<String,HashMap<ItemStack, List<String>>>();
		    	permSlots.put(permission, buttons);
		    	buttonSlots.put(AnvilSlot.OUTPUT, permSlots);
		    	
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
}