package me.drkmatr1984.anvilstringcommand;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AnvilLang
{
  String PLPrefix = "§7[§1AnvilCommands§7]§r ";
  String PluginName = "AnvilCommands";
  String NoPerms = "§cYou don't have Permission§r";
  String Reloaded = "§eAnvilCommands has been Reloaded!";
  String Version = "§5Version§r";
  
  String Help = " Help  ";
  String DisplayHelp = "- Displays this Help";
  String VersionHelp = " - Shows the Plugin Version Number";
  String Reloads = " - Reloads the Plugin Config";
  String AdminHelp = " Admin Help ";
  
  String UserPerm = "anvilcommands.user";
  String AdminPerm = "anvilcommands.admin";
  
  String mainCommand = "anvilcommands";
  Set<String> aliases = new HashSet<String>();
  
  AnvilStringCommand anvil = AnvilStringCommand.getInstance();
  File lf = new File(this.anvil.getDataFolder().toString() + "/lang");
  private File languageFile = new File(this.lf, "language.yml");
  FileConfiguration language;
  
  public void loadLanguageFile()
  {
    if (!this.lf.exists()) {
      this.lf.mkdir();
    }
    if (!this.languageFile.exists()) {
      this.anvil.saveResource("lang/language.yml", false);
    }
    this.language = YamlConfiguration.loadConfiguration(this.languageFile);
  }
  
  public void InitializeLang()
  {
    loadLanguageFile();
    if ((this.language.getString("General.ChatPrefix") != null) && (this.language.getString("General.ChatPrefix") != "")) {
      this.PLPrefix = formatColor(this.language.getString("General.ChatPrefix"));
    }
    if ((this.language.getString("General.PluginName") != null) && (this.language.getString("General.PluginName") != "")) {
      this.PluginName = formatColor(this.language.getString("General.PluginName"));
    }
    if ((this.language.getString("General.NoPerms") != null) && (this.language.getString("General.NoPerms") != "")) {
      this.NoPerms = formatColor(this.language.getString("General.NoPerms"));
    }
    if ((this.language.getString("General.Reloaded") != null) && (this.language.getString("General.Reloaded") != "")) {
      this.Reloaded = formatColor(this.language.getString("General.Reloaded"));
    }
    if ((this.language.getString("General.Version") != null) && (this.language.getString("General.Version") != "")) {
      this.Version = formatColor(this.language.getString("General.Version"));
    }
    
    if ((this.language.getString("Help.Help") != null) && (this.language.getString("Help.Help") != "")) {
      this.Help = formatColor(this.language.getString("Help.Help"));
    }
    if ((this.language.getString("Help.DisplayHelp") != null) && (this.language.getString("Help.DisplayHelp") != "")) {
      this.DisplayHelp = formatColor(this.language.getString("Help.DisplayHelp"));
    }
    if ((this.language.getString("Help.Version") != null) && (this.language.getString("Help.Version") != "")) {
      this.VersionHelp = formatColor(this.language.getString("Help.Version"));
    }
    if ((this.language.getString("Help.Reloads") != null) && (this.language.getString("Help.Reloads") != "")) {
      this.Reloads = formatColor(this.language.getString("Help.Reloads"));
    }
    if ((this.language.getString("Help.AdminHelp") != null) && (this.language.getString("Help.AdminHelp") != "")) {
      this.AdminHelp = formatColor(this.language.getString("Help.AdminHelp"));
    }
    
    if ((this.language.getString("Permissions.UserPerm") != null) && (this.language.getString("Permissions.UserPerm") != "")) {
      this.UserPerm = ChatColor.stripColor(this.language.getString("Permissions.UserPerm"));
    }
    if ((this.language.getString("Permissions.AdminPerm") != null) && (this.language.getString("Permissions.AdminPerm") != "")) {
      this.AdminPerm = ChatColor.stripColor(this.language.getString("Permissions.AdminPerm"));
    }
    
    if ((this.language.getString("PluginCommands.Main.Command") != null) && (this.language.getString("PluginCommands.Main.Command") != "")) {
      this.mainCommand = ChatColor.stripColor(this.language.getString("PluginCommands.Main.Command"));
    }
    if (!this.language.getStringList("PluginCommands.Main.Aliases").isEmpty()) {
      List<String> temp = this.language.getStringList("PluginCommands.Main.Aliases");
      for (String s : temp)
      {
        if ((!s.equals(null)) || (!s.equals(""))) {
          this.aliases.add(ChatColor.stripColor(s));
        }
      }
    }
  }
  

  public String formatColor(String words)
  {
    String temp = "";
    if ((!words.equals(null)) && (!words.equals(""))) {
      if (words.contains("&")) {
        temp = ChatColor.translateAlternateColorCodes('&', words);
        return temp;
      }
    } else {
      return "";
    }
    return words;
  }
}
