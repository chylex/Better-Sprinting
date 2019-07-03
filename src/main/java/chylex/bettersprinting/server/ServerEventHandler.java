package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
@EventBusSubscriber(value = Side.SERVER, modid = BetterSprintingMod.modId)
public final class ServerEventHandler{
	@SubscribeEvent
	public static void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.player);
	}
	
	private ServerEventHandler(){}
}
