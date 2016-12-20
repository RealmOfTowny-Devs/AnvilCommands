package me.drkmatr1984.AnvilCommands.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.drkmatr1984.AnvilCommands.AnvilCommandExecutor.RunLevel;
import me.drkmatr1984.AnvilCommands.AnvilCommands;

public class CommandTask extends BukkitRunnable{
	
	private RunLevel level;
	private AnvilCommands plugin;
	private Player player;
	private String com;
	
	public CommandTask(RunLevel level, AnvilCommands plugin, Player player, String com){
		this.level = level;
		this.plugin = plugin;
		this.player = player;
		this.com = com;
	}
	
	@Override
	public void run() {
		if(this.level==RunLevel.PLAYER) {
			this.player.performCommand(com);
		}
        if(this.level==RunLevel.OP){
        	if (!this.player.isOp()) {
    			this.player.setOp(true);
    			this.player.performCommand(com);
    			this.player.setOp(false);
    		} else {
    			this.player.performCommand(com);
    		}
        }		
        if(this.level==RunLevel.CONSOLE){
        	this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), com);
        }
	}
	
}