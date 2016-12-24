package me.drkmatr1984.AnvilCommands.utils;

import org.bukkit.Bukkit;

public class ReflectionUtils
{
	  public static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
		   return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	  }

	  public static Class<?> getCraftBukkitClass(String nmsClassName) throws ClassNotFoundException {
		   return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	  }
}