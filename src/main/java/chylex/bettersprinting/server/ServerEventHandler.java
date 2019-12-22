package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = BetterSprintingMod.id)
public final class ServerEventHandler{
	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent e){
		ServerCommandConfig.register(e.getCommandDispatcher());
	}
	
	@SubscribeEvent
	public static void onPlayerLogout(PlayerLoggedOutEvent e){
		ServerNetwork.onDisconnected(e.getPlayer());
	}
}
