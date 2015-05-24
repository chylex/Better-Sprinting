package chylex.bettersprinting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public abstract class BetterSprintingProxy{
	public abstract void loadSidedConfig(BetterSprintingConfig config);
	public abstract void onPreInit(FMLPreInitializationEvent e);
	public abstract void onInit(FMLInitializationEvent e);
	public abstract void onServerStarting(FMLServerStartingEvent e);
}
