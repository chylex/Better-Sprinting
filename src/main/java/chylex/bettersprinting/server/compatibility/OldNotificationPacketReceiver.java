package chylex.bettersprinting.server.compatibility;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import chylex.bettersprinting.server.ServerSettings;

public class OldNotificationPacketReceiver{
	private static OldNotificationPacketReceiver instance;
	
	public static void register(){
		instance = new OldNotificationPacketReceiver();
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static void kickOldModUsers(){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		
		for(UUID id:instance.users.keySet()){
			EntityPlayerMP player = manager.getPlayerByUUID(id);
			if (player != null)player.playerNetServerHandler.kickPlayerFromServer("The server does not allow Better Sprinting. Newer versions of the mod can be disabled automatically without kicking.");
		}
		
		instance.users.clear();
	}
	
	private final FMLEventChannel channel;
	private final TObjectIntHashMap<UUID> users = new TObjectIntHashMap<UUID>();
	
	private OldNotificationPacketReceiver(){
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("BSprint");
		channel.register(this);
	}
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent e){
		ByteBuf data = e.packet.payload();
		
		if (data.readByte() == 4){
			NetHandlerPlayServer net = (NetHandlerPlayServer)e.handler;
			users.put(net.playerEntity.getUniqueID(),100);
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase == Phase.END || !ServerSettings.disableClientMod)return;
		
		Set<UUID> ids = new HashSet<UUID>(users.keySet());
		
		for(UUID id:ids){
			int val = users.adjustOrPutValue(id,-1,0);
			
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(id);
			
			if (player == null){
				users.remove(id);
				continue;
			}
			
			if (val > 10 && val < 80){
				if (player.playerNetServerHandler.netManager.channel().isWritable())users.put(id,val = 10);
			}
			
			if (val <= 0){
				users.remove(id);
				player.playerNetServerHandler.kickPlayerFromServer("The server does not allow Better Sprinting. Newer versions of the mod can be disabled automatically without kicking.");
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDisconnect(PlayerLoggedOutEvent e){
		users.remove(e.player.getUniqueID());
	}
}
