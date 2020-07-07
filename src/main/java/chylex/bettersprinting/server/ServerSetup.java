package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public final class ServerSetup{
	public static void setup(){
		BetterSprintingConfig.initialize(ModConfig.Type.COMMON, ServerSettings.getSpec(), "server");
		BetterSprintingNetwork.initialize(new ServerNetwork());
		
		MinecraftForge.EVENT_BUS.register(ServerSetup.class);
		MinecraftForge.EVENT_BUS.register(ServerNetwork.class);
	}
	
	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent e){
		ServerCommandConfig.register(e.getCommandDispatcher());
	}
}
