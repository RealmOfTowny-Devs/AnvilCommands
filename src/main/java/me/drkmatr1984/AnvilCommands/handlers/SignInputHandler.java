package me.drkmatr1984.AnvilCommands.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.drkmatr1984.AnvilCommands.events.SignInputEvent;
import me.drkmatr1984.AnvilCommands.utils.ReflectionUtils;
import net.minecraft.server.v1_11_R1.Packet;
import net.minecraft.server.v1_11_R1.PacketPlayInUpdateSign;

public class SignInputHandler {

	static Field channelField;
	
 
    static {
    	
    	Class<?> networkmanager;
    	try {
			networkmanager = ReflectionUtils.getNmsClass("NetworkManager");			
			Method getdeclaredfields = networkmanager.getClass().getMethod("getDeclaredFields");
			Field[] fields;
			try {
				fields = (Field[]) getdeclaredfields.invoke(networkmanager);
				if(fields!=null){
					for (Field field : fields) {
			            if (field.getType().isAssignableFrom(Channel.class)) {
			                channelField = field;
			                break;
			            }
			        }
				}			
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			Bukkit.getServer().getLogger().info("A ClassNotFoundException has occurred in the SignInputHandler. Are you using a supported version?");
			e.printStackTrace();
		}
        
    }
    
    public static void openSignGUI(Player pl)
    {
    	Integer x = pl.getLocation().getBlockX();
		Integer y = pl.getLocation().getBlockY();
		Integer z = pl.getLocation().getBlockZ();           		  
	    Class<?> blockposition;
	    Class<?> signPacket;
	    try {
			blockposition = ReflectionUtils.getNmsClass("BlockPosition");
			signPacket = ReflectionUtils.getNmsClass("PacketPlayOutOpenSignEditor");
			Method getHandle = pl.getClass().getMethod("getHandle");
			Object nmsPlayer = getHandle.invoke(pl);
			Constructor<?> blockPosCon = blockposition.getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE });
	        Object blockPosition = blockPosCon.newInstance(new Object[] { x, y, z });
	        Constructor<?> sPacketConstructor = signPacket.getConstructor(blockposition);
	        Object packet = sPacketConstructor.newInstance(blockPosition);
	        Field conField = nmsPlayer.getClass().getField("playerConnection");
			Object con = conField.get(nmsPlayer);
			Method sendPacket = ReflectionUtils.getNmsClass("PlayerConnection").getMethod("sendPacket", ReflectionUtils.getNmsClass("Packet"));
		    sendPacket.invoke(con, packet);
		    SignInputHandler.injectNetty(pl);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    }
    
    @SuppressWarnings("rawtypes")
    public static void injectNetty(final Player player) {
    	try {
            Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
            if (channel != null) {
                channel.pipeline().addAfter("decoder", "update_sign", new MessageToMessageDecoder<Packet>() {
                 
                    @Override
                    protected void decode(ChannelHandlerContext chc, Packet packet, List<Object> out) throws Exception {
                        if (packet instanceof PacketPlayInUpdateSign) {
                         
                            PacketPlayInUpdateSign usePacket = (PacketPlayInUpdateSign) packet;
                            Bukkit.getPluginManager().callEvent(new SignInputEvent.SignGUICloseEvent(player, usePacket.b()));
                        }
                        out.add(packet);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void ejectNetty(Player player) {
        try {
        	Method getHandle = player.getClass().getMethod("getHandle");
			Object nmsPlayer = getHandle.invoke(player);
			Field conField = nmsPlayer.getClass().getField("playerConnection");
			Object con = conField.get(nmsPlayer);
            Channel channel = (Channel) channelField.get(con.getClass().getField("networkManager"));
            if (channel != null) {
                if (channel.pipeline().get("update_sign") != null) {
                    channel.pipeline().remove("update_sign");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}