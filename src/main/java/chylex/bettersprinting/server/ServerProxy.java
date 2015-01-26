package chylex.bettersprinting.server;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;

public class ServerProxy extends BetterSprintingProxy{
	@Override
	public void loadSidedConfig(BetterSprintingConfig config){}
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e){
		PacketPipeline.initialize(new ServerNetwork());
	}

	@Override
	public void onInit(FMLInitializationEvent e){}
}
