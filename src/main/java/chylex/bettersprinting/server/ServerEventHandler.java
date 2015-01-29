package chylex.bettersprinting.server;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
