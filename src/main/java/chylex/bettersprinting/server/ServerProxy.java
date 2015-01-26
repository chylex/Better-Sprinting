package chylex.bettersprinting.server;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingProxy;

public class ServerProxy extends BetterSprintingProxy{
	@Override
	public void loadSidedConfig(BetterSprintingConfig config){}
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e){}

	@Override
	public void onInit(FMLInitializationEvent e){}
}
