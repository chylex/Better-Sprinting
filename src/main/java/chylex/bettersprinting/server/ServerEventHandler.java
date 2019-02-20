package chylex.bettersprinting.server;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@OnlyIn(Dist.DEDICATED_SERVER)
final class ServerEventHandler{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}
	
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent e){
		ServerProxy.mcVersion = e.getServer().getMinecraftVersion();
		ServerCommandConfig.register(e.getCommandDispatcher());
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.getPlayer());
	}
	
	private ServerEventHandler(){}
}
