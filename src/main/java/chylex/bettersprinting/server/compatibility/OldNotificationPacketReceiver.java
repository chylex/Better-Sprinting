package chylex.bettersprinting.server.compatibility;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentTranslation;
import chylex.bettersprinting.server.ServerSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
			if (player != null)kickPlayer(player.playerNetServerHandler,"bs.server.kick");
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
			
			if (ServerSettings.disableClientMod)kickPlayer(handler,"bs.server.kick");
			else users.add(handler.playerEntity.getCommandSenderName());
		}
	}
	
	@SubscribeEvent
	public void onPlayerDisconnect(PlayerLoggedOutEvent e){
		users.remove(e.player.getCommandSenderName());
	}
	
	private static void kickPlayer(final NetHandlerPlayServer netHandler, String translationName){
		final ChatComponentTranslation msg = new ChatComponentTranslation(translationName);
		
		netHandler.netManager.scheduleOutboundPacket(new S40PacketDisconnect(msg),new GenericFutureListener[]{
			new GenericFutureListener(){
				@Override
				public void operationComplete(Future isBright){
					netHandler.netManager.closeChannel(msg);
				}
			}
		});
		
		netHandler.netManager.disableAutoRead();
	}
}
