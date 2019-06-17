package chylex.bettersprinting.server;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public final class ServerEventHandler{
	public static void register(){
		FMLCommonHandler.instance().bus().register(new ServerEventHandler());
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.player);
	}
	
	private ServerEventHandler(){}
}
