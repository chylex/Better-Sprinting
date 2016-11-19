package chylex.bettersprinting.server;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public final class ServerEventHandler{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.player);
	}
	
	private ServerEventHandler(){}
}
