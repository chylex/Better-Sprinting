package chylex.bettersprinting.server;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SideOnly(Side.SERVER)
final class ServerNetwork implements INetworkHandler{
	/*
	 * OVERVIEW
	 * ========
	 * Use this guide to handle players with Better Sprinting in Minecraft 1.9. If you need information for older versions,
	 * view this file in the 1.8.8 branch where you can find a guide to disable the mod in all legacy versions of the mod.
	 *
	 * Better Sprinting for Minecraft 1.9 no longer uses the legacy BSprint channel, if you are using unofficial mods or
	 * plugins to ban the mod from your server, please make sure it takes advantage of the new BSM channel features which
	 * allow seamlessly disabling the mod when a player enters your server.
	 *
	 *
	 *
	 * INCOMING PACKETS
	 * ================
	 * Payload packet on "BSM" channel, sent when a player connects to the server
	 *
	 *** byte 0, byte <protocolVersion> - the protocol version can be used to determine available functionality (see below)
	 *
	 *
	 *
	 * OUTCOMING PACKETS
	 * =================
	 * Payload packet on "BSM" channel, sent when a player connects to the server or the server settings change.
	 *
	 *** byte 0, boolean <enableSurvivalFlyBoost>, boolean <enableAllDirs> - custom settings, both are false by default [since 10]
	 *** byte 1 - disables the mod on client-side [since 10]
	 *** byte 2 - re-enables the mod in client-side, called from command [since 10]
	 *
	 *
	 *
	 * ADDITIONAL INFO
	 * ===============
	 * Feel free to contact me if you need details on how to implement special cases, such as servers that accept clients
	 * from older Minecraft versions. The Forge version of Better Sprinting is the only official server mod, I cannot
	 * guarantee support of the protocol features in any unofficial mods or plugins.
	 */
	
	private static final Set<UUID> players = Collections.synchronizedSet(new HashSet<>());
	
	public static boolean hasBetterSprinting(EntityPlayer player){
		return players.contains(player.getUniqueID());
	}
	
	public static void onDisconnected(EntityPlayer player){
		players.remove(player.getUniqueID());
	}
	
	public static PacketBuffer writeSettings(boolean enableSurvivalFlyBoost, boolean enableAllDirs){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeBoolean(enableSurvivalFlyBoost).writeBoolean(enableAllDirs);
		return buffer;
	}
	
	public static PacketBuffer writeDisableMod(boolean disable){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(disable ? 1 : 2);
		return buffer;
	}
	
	public static void sendToPlayer(EntityPlayer player, PacketBuffer packet){
		if (hasBetterSprinting(player)){
			PacketPipeline.sendToPlayer(packet, player);
		}
	}
	
	public static void sendToAll(List<? extends EntityPlayer> players, PacketBuffer packet){
		for(EntityPlayer player:players){
			sendToPlayer(player, packet);
		}
	}
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){
		players.add(player.getUniqueID());
		
		if (ServerSettings.disableClientMod){
			sendToPlayer(player, writeDisableMod(true));
		}
		
		if (ServerSettings.enableSurvivalFlyBoost || ServerSettings.enableAllDirs){
			sendToPlayer(player, writeSettings(ServerSettings.enableSurvivalFlyBoost, ServerSettings.enableAllDirs));
		}
	}
}
