package chylex.bettersprinting.server;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.DEDICATED_SERVER)
final class ServerNetwork implements INetworkHandler{
	/*
	 * OVERVIEW
	 * ========
	 * Use this guide to handle players with Better Sprinting in Minecraft 1.13. If you need information for older versions,
	 * view this file in the appropriate branch where you can find a guide to disable the mod in legacy versions of the mod.
	 *
	 * All messages are sent via payload packets on the "bsm:settings" channel (versions before 1.13 used a different name).
	 * See https://wiki.vg/Plugin_channels for information about payload packets.
	 *
	 * Each message begins with a byte that identifies its type.
	 * Some message types declare additional parameters that must follow after the first byte.
	 *
	 *
	 *
	 * INCOMING PACKETS
	 * ================
	 *
	 *** [byte 0] [byte <protocolVersion>]
	 ***
	 *** Asks the server to send it any necessary information.
	 *** The <protocolVersion> parameter may be used in the future to inform server about client's feature availability.
	 *** Sent when a player connects to the server.
	 *
	 *
	 *
	 * OUTCOMING PACKETS
	 * =================
	 *
	 *** [byte 0] [bool <enableSurvivalFlyBoost>] [bool <enableAllDirs>]
	 ***
	 *** Notifies the player about which non-vanilla settings are enabled on the server (both are disabled by default).
	 *** Sent to player when their [byte 0] message is processed, and either or both settings are enabled.
	 *** Sent to all players with the mod after using the '/bettersprinting setting (...)' command.
	 *
	 *
	 *** [byte 1]
	 ***
	 *** Disables basic functionality of the mod on client side.
	 *** Sent to player when their [byte 0] message is processed, and the server wants to disable the mod.
	 *** Sent to all players with the mod after using the '/bettersprinting disablemod true' command.
	 *
	 *
	 *** [byte 2]
	 ***
	 *** Re-enables basic functionality of the mod on client side.
	 *** Sent to all players with the mod after using the '/bettersprinting disablemod false' command.
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
	
	public static boolean hasBetterSprinting(PlayerEntity player){
		return players.contains(player.getUniqueID());
	}
	
	public static void onDisconnected(PlayerEntity player){
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
	
	public static void sendToPlayer(PlayerEntity player, PacketBuffer packet){
		if (hasBetterSprinting(player)){
			PacketPipeline.sendToPlayer(packet, player);
		}
	}
	
	public static void sendToAll(List<? extends PlayerEntity> players, PacketBuffer packet){
		for(PlayerEntity player:players){
			sendToPlayer(player, packet);
		}
	}
	
	@Override
	public void onPacket(LogicalSide side, ByteBuf data, PlayerEntity player){
		players.add(player.getUniqueID());
		
		if (ServerSettings.disableClientMod.get()){
			sendToPlayer(player, writeDisableMod(true));
		}
		
		if (ServerSettings.enableSurvivalFlyBoost.get() || ServerSettings.enableAllDirs.get()){
			sendToPlayer(player, writeSettings(ServerSettings.enableSurvivalFlyBoost.get(), ServerSettings.enableAllDirs.get()));
		}
	}
}
