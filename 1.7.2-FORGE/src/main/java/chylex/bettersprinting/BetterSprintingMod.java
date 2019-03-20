package chylex.bettersprinting;
import org.apache.logging.log4j.Logger;
import chylex.bettersprinting.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="bettersprinting", name="Better Sprinting", version="1.0")
public class BetterSprintingMod{
	@Instance("bettersprinting")
	public static BetterSprintingMod instance;
	
	@SidedProxy(clientSide="chylex.bettersprinting.proxy.ClientProxy", serverSide="chylex.bettersprinting.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	public static String modVersion;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		modVersion=e.getModMetadata().version;
		logger=e.getModLog();
		proxy.registerEvents(e);
	}

	@EventHandler
	public void onInit(FMLInitializationEvent e){
		proxy.initMod();
	}
}
