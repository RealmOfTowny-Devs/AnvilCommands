/**
 * This file uses part of AnvilPatch, licensed under the MIT License (MIT).
 *
 * Copyright (c) Cybermaxke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.drkmatr1984.anvilstringcommand.v18r1;

import net.minecraft.server.v1_8_R1.ChatMessage;
import net.minecraft.server.v1_8_R1.ContainerAnvil;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R1.PacketPlayOutSetSlot;
import me.drkmatr1984.anvilstringcommand.util.ReflectionUtils;
import me.drkmatr1984.anvilstringcommand.v18r1.AnvilContainer;
import net.minecraft.server.v1_8_R1.ItemStack;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import me.drkmatr1984.anvilstringcommand.AnvilGUI;
import java.util.HashMap;

/**
 * Programmed by Tevin on 8/8/2015.
 */
public class SAnvilGUI implements AnvilGUI, Listener{
    private Player player;
    private AnvilClickEventHandler handler;
    private HashMap<AnvilSlot, org.bukkit.inventory.ItemStack> items = new HashMap<AnvilSlot, org.bukkit.inventory.ItemStack>();
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
                        org.bukkit.inventory.ItemStack item = event.getCurrentItem();
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
                            destroy();
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

    public void setSlot(AnvilSlot slot, org.bukkit.inventory.ItemStack item) {
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
        player = null;
        handler = null;
        items = null;
        HandlerList.unregisterAll(listener);
        listener = null;
    }
    
    private void tryPatch(Player p){	    
		patchGUI(p);
    }
    
    private void tryPatch(EntityPlayer p){
    	tryPatch(p.getBukkitEntity());
    }
    
    private Field fieldChannel;
	
	@Override
	public void patchGUI(Player player) {
		// TODO Auto-generated method stub
		this.inject(player, true);
	}

	/**
	 * Gets the session of the entity player.
	 * 
	 * @param entityPlayer the player
	 * @return the channel
	 * @throws Exception
	 */
	public Channel getChannel(EntityPlayer entityPlayer) throws Exception {
		NetworkManager nm = entityPlayer.playerConnection.networkManager;

		if (this.fieldChannel == null) {
			this.fieldChannel = ReflectionUtils.findField(NetworkManager.class, Channel.class, 0);
		}

		this.fieldChannel.setAccessible(true);
		return (Channel) this.fieldChannel.get(nm);
	}

	/**
	 * Injects or uninjects the message handler from the player.
	 * 
	 * @param player the player
	 * @param inject whether it should be injected
	 */
	private void inject(Player player, boolean inject) {
		try {
			this.inject0(player, inject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void inject0(Player player, boolean inject) throws Exception {
		EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();

		Channel channel = this.getChannel(playerHandle);
		ChannelPipeline pipe = channel.pipeline();

		// Try to find the handler name
		String handler = this.findHandler(pipe);

		if (handler == null) {
			throw new IllegalStateException("unable to find the minecraft packet handler");
		}

		if (inject && pipe.get("anvilpatch") == null) {
			pipe.addBefore(handler, "anvilpatch", new Handler(playerHandle));
		} else if (!inject && pipe.get("anvilpatch") != null) {
			pipe.remove("anvilpatch");
		}
	}

	public String findHandler(ChannelPipeline pipe) {
		// This should normally be good enough
		if (pipe.get("packet_handler") != null) {
			return "packet_handler";
		}
		// Try to find the network manager under a different name
		for (Entry<String, ChannelHandler> en : pipe.toMap().entrySet()) {
			if (en.getValue() instanceof NetworkManager) {
				return en.getKey();
			}
		}
		// Not found :(
		return null;
	}

	private static final class Handler extends ChannelDuplexHandler {
		private final EntityPlayer playerHandle;
		private final Thread mainThread;

		public Handler(EntityPlayer playerHandle) {
			this.playerHandle = playerHandle;
			// The patch is applied in the main thread
			this.mainThread = Thread.currentThread();
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof PacketPlayInCustomPayload) {
				final PacketPlayInCustomPayload msg0 = (PacketPlayInCustomPayload) msg;
				if (msg0.a().equals("MC|ItemName")) {
					BukkitRunnable runnable = new BukkitRunnable() {

						@Override
						public void run() {
							handleItemRename(msg0);
						}

					};
					if (Thread.currentThread() == this.mainThread) {
						runnable.run();
					} else {
						runnable.runTask(Bukkit.getPluginManager().getPlugins()[0]);
					}
					return;
				}
			}
			super.channelRead(ctx, msg);
		}

		public void handleItemRename(PacketPlayInCustomPayload msg) {
			ContainerAnvil container;
			if(this.playerHandle.activeContainer instanceof AnvilContainer)
			{
				container = (ContainerAnvil) this.playerHandle.activeContainer;
				String value = "";
				if (msg.b() != null && msg.b().readableBytes() >= 1) {
					value = msg.b().c(Short.MAX_VALUE);
					StringBuilder builder = new StringBuilder();
					for (char c : value.toCharArray()) {
						if (c >= ' ' && c != '\u0000') {
							builder.append(c);
						}
					}
					value = ChatColor.translateAlternateColorCodes('&', builder.toString());
					if (value.length() <= 30) {
						container.a(value); //What was here
						ItemStack itemStack0 = container.getSlot(0).getItem();
						ItemStack itemStack1 = container.getSlot(1).getItem();
						ItemStack itemStack2 = container.getSlot(2).getItem();
						if (itemStack0 != null) {
							if (StringUtils.isEmpty(value)) {    //if nothing is typed this sets the result to the
								if (itemStack0.hasName()) {      //name of the item in slot1
									if(itemStack2 != null)								
										itemStack2.r();
									}
							} else if (!value.equals(itemStack0.getName())) {
								if (itemStack2 == null) {
									itemStack2 = itemStack0.cloneItemStack();
									container.getSlot(2).set(itemStack2);
								}
								itemStack2.c(value);
							} else if (itemStack1 == null || itemStack1.getItem() == null) {
								PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(container.windowId, 2, null);
								this.playerHandle.playerConnection.sendPacket(packet);
								container.getSlot(2).set(null);
							}
						}
					}
				} else {
					container.a("");
				}
			} 									
		}
	}
}