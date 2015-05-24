package chylex.bettersprinting.server;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import chylex.bettersprinting.system.PacketPipeline;

@SideOnly(Side.SERVER)
public final class ServerEventHandler{
	public static void register(){
		FMLCommonHandler.instance().bus().register(new ServerEventHandler());
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
	
	private ServerEventHandler(){}
}
