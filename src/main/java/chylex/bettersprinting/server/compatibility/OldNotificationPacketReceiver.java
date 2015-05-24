package chylex.bettersprinting.server.compatibility;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import chylex.bettersprinting.server.ServerSettings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class OldNotificationPacketReceiver{
	private static OldNotificationPacketReceiver instance;
	
	public static void register(){
		instance = new OldNotificationPacketReceiver();
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static void kickOldModUsers(){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		
		for(String id:instance.users){
			EntityPlayerMP player = manager.func_152612_a(id);
			if (player != null)player.playerNetServerHandler.kickPlayerFromServer("The server does not allow Better Sprinting. Newer versions of the mod can be disabled automatically without kicking.");
		}
		
		instance.users.clear();
	}
	
	private final FMLEventChannel channel;
	private final Set<String> users = new HashSet<>();
	
	private OldNotificationPacketReceiver(){
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("BSprint");
		channel.register(this);
	}
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent e){
		ByteBuf data = e.packet.payload();
		
		if (data.readByte() == 4){
			NetHandlerPlayServer handler = (NetHandlerPlayServer)e.handler;
			
			if (ServerSettings.disableClientMod)handler.kickPlayerFromServer("The server does not allow Better Sprinting. Newer versions of the mod can be disabled automatically without kicking.");
			else users.add(handler.playerEntity.getCommandSenderName());
		}
	}
	
	@SubscribeEvent
	public void onPlayerDisconnect(PlayerLoggedOutEvent e){
		users.remove(e.player.getCommandSenderName());
	}
}
