/**
 * This file is part of AnvilPatch, licensed under the MIT License (MIT).
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
package me.drkmatr1984.anvilstringcommand.v18r2;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.drkmatr1984.anvilstringcommand.AnvilPatcher;
import me.drkmatr1984.anvilstringcommand.util.ReflectionUtils;
import net.minecraft.server.v1_8_R2.ItemStack;
import net.minecraft.server.v1_8_R2.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.NetworkManager;
import net.minecraft.server.v1_8_R2.PacketPlayInCustomPayload;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SAnvilPatcher implements AnvilPatcher, Listener {
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
			AnvilContainer container = (AnvilContainer) this.playerHandle.activeContainer;

			if (msg.b() != null && msg.b().readableBytes() >= 1) {
				String value = msg.b().c(Short.MAX_VALUE);
				StringBuilder builder = new StringBuilder();
				for (char c : value.toCharArray()) {
					if (c >= ' ' && c != '\u0000') {
						builder.append(c);
					}
				}
				value = ChatColor.translateAlternateColorCodes('&', builder.toString());
				if (value.length() <= 30) {
					container.a(value);
					ItemStack itemStack0 = container.getSlot(0).getItem();
					ItemStack itemStack1 = container.getSlot(1).getItem();
					ItemStack itemStack2 = container.getSlot(2).getItem();
					if (itemStack0 != null) {
						if (StringUtils.isEmpty(value)) {
							if (itemStack0.hasName()) {
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
