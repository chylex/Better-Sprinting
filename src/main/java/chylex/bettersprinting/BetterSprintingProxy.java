package chylex.bettersprinting;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public abstract class BetterSprintingProxy{
	public abstract void loadSidedConfig(BetterSprintingConfig config);
	public abstract void onPreInit(FMLPreInitializationEvent e);
	public abstract void onInit(FMLInitializationEvent e);
	public abstract void onServerStarting(FMLServerStartingEvent e);
}
