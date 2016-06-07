package me.drkmatr1984.anvilstringcommand.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.drkmatr1984.anvilstringcommand.AnvilCommandExecutor.RunLevel;
import me.drkmatr1984.anvilstringcommand.AnvilStringCommand;

public class CommandTask extends BukkitRunnable{
	
	private RunLevel level;
	private AnvilStringCommand plugin;
	private Player player;
	private String com;
	
	public CommandTask(RunLevel level, AnvilStringCommand plugin, Player player, String com){
		this.level = level;
		this.plugin = plugin;
		this.player = player;
		this.com = com;
	}
	
	@Override
	public void run() {
		switch (level) {
        case PLAYER: 
          this.player.performCommand(com);
        case OP: 
          if (!this.player.isOp()) {
            this.player.setOp(true);
            this.player.performCommand(com);
            this.player.setOp(false);
          } else {
            this.player.performCommand(com);
          }
        case CONSOLE: 
          this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), com);
        }	
	}
	
}