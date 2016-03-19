package chylex.bettersprinting.server;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.system.PacketPipeline;

@SideOnly(Side.SERVER)
public final class ServerEventHandler{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		if (ServerSettings.disableClientMod){
			PacketPipeline.sendToPlayer(ServerNetwork.writeDisableMod(true),e.player);
		}
		else if (ServerSettings.enableSurvivalFlyBoost || ServerSettings.enableAllDirs){
			PacketPipeline.sendToPlayer(ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost,ServerSettings.enableAllDirs),e.player);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.player);
	}
	
	private ServerEventHandler(){}
}
