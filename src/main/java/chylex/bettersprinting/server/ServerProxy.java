package chylex.bettersprinting.server;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;

public class ServerProxy extends BetterSprintingProxy{
	@Override
	public void loadSidedConfig(BetterSprintingConfig config){
		ServerSettings.reload(config);
	}
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e){
		ServerEventHandler.register();
		PacketPipeline.initialize(new ServerNetwork());
	}

	@Override
	public void onInit(FMLInitializationEvent e){}
	
	@Override
	public void onServerStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new ServerCommandConfig());
	}
}
