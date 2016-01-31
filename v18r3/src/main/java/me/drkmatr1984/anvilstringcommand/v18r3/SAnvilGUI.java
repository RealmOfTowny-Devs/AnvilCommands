package me.drkmatr1984.anvilstringcommand.v18r3;


import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import me.drkmatr1984.anvilstringcommand.v18r3.AnvilContainer;
import me.drkmatr1984.anvilstringcommand.v18r3.SAnvilPatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import me.drkmatr1984.anvilstringcommand.AnvilGUI;
import me.drkmatr1984.anvilstringcommand.AnvilPatcher;

import java.util.HashMap;

/**
 * Programmed by Tevin on 8/8/2015.
 */
public class SAnvilGUI implements AnvilGUI{
    private Player player;
    private AnvilClickEventHandler handler;
    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
    private Inventory inv;
    private Listener listener;
    private JavaPlugin plugin;
    
    public SAnvilGUI(Player player, JavaPlugin plugin, final AnvilClickEventHandler handler) {
        this.player = player;
        this.handler = handler;
        this.plugin = plugin;
        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    Player clicker = (Player) event.getWhoClicked();
                    if (event.getInventory().equals(inv)) {
                        event.setCancelled(true);
                        ItemStack item = event.getCurrentItem();
                        int slot = event.getRawSlot();
                        String name = "";
                        if (item != null) {
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta.hasDisplayName()) {
                                    name = meta.getDisplayName();
                                }
                            }
                        }
                        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                        handler.onAnvilClick(clickEvent);
                        if (clickEvent.getWillClose()) {
                            event.getWhoClicked().closeInventory();
                        }
                        if (clickEvent.getWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Player player = (Player) event.getPlayer();
                    Inventory inv = event.getInventory();
                    if (inv.equals(SAnvilGUI.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }
            }
            
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    destroy();
                }
            }
        };
        if(!plugin.isEnabled()){
        	plugin.getPluginLoader().enablePlugin(plugin); 	
        }
        Bukkit.getPluginManager().registerEvents(listener, plugin); //Replace with instance of main class
    }
    
    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        AnvilContainer container = new AnvilContainer(p);   
        //Set the items to the items from the inventory given
        inv = container.getBukkitView().getTopInventory();       
        for (AnvilSlot slot : items.keySet()) {      	
        	inv.setItem(slot.getSlot(), items.get(slot));
        }
        //Counter stuff that the game uses to keep track of inventories
        int c = p.nextContainerCounter();           
        //Send the packet
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        //Set their active container to the container
        p.activeContainer = container;
        //Set their active container window id to that counter stuff
        p.activeContainer.windowId = c;
        //Add the slot listener
        p.activeContainer.addSlotListener(p);
        if((plugin.getServer().getPluginManager().getPlugin("AnvilPatch") == null) || (!plugin.getServer().getPluginManager().isPluginEnabled("AnvilPatch")))
        	tryPatch(p);
    }

    public void destroy() {
    	if(player!=null && ((plugin.getServer().getPluginManager().getPlugin("AnvilPatch") == null) || (!plugin.getServer().getPluginManager().isPluginEnabled("AnvilPatch"))))
    		unPatch(player);
        player = null;
        handler = null;
        items = null;
        HandlerList.unregisterAll(listener);
        listener = null;
    }
    
    private void unPatch(Player p){
	    AnvilPatcher patcher;
		patcher = (AnvilPatcher) new SAnvilPatcher();
		patcher.unpatchGUI(p);
	}
    
    private void tryPatch(Player p){
	    AnvilPatcher patcher;
		patcher = (AnvilPatcher) new SAnvilPatcher();
		patcher.patchGUI(p);
    }
    
    private void tryPatch(EntityPlayer p){
    	tryPatch(p.getBukkitEntity());
    }
}