package chylex.bettersprinting.server;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.server.compatibility.OldNotificationPacketReceiver;
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
		OldNotificationPacketReceiver.register();
	}

	@Override
	public void onInit(FMLInitializationEvent e){}
	
	@Override
	public void onServerStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new ServerCommandConfig());
	}
}
