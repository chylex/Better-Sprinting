package chylex.bettersprinting;
import chylex.bettersprinting.system.Log;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid="BetterSprinting", name="Better Sprinting", useMetadata = true, guiFactory = "chylex.bettersprinting.client.gui.ModGuiFactory", acceptableRemoteVersions = "*")
public class BetterSprintingMod{
	@Instance("BetterSprinting")
	public static BetterSprintingMod instance;
	
	@SidedProxy(clientSide="chylex.bettersprinting.client.ClientProxy", serverSide="chylex.bettersprinting.server.ServerProxy")
	public static BetterSprintingProxy proxy;
	
	public static BetterSprintingConfig config;
	
	public static final String buildId = "24-05-2015-0";
	public static String modVersion;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		Log.load();
		modVersion = e.getModMetadata().version;
		config = new BetterSprintingConfig(e.getSuggestedConfigurationFile());
		proxy.onPreInit(e);
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		proxy.onInit(e);
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e){
		proxy.onServerStarting(e);
	}
}
