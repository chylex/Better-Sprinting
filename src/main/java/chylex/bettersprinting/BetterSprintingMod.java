package chylex.bettersprinting;
import java.util.Map;
import chylex.bettersprinting.system.Log;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = BetterSprintingMod.modId,
     version = BetterSprintingMod.modVersion,
     name = "Better Sprinting",
     useMetadata = true,
     guiFactory = "chylex.bettersprinting.client.gui.ModGuiFactory",
     acceptableRemoteVersions = "*")
public class BetterSprintingMod{
	@SidedProxy(clientSide = "chylex.bettersprinting.client.ClientProxy", serverSide = "chylex.bettersprinting.server.ServerProxy")
	public static BetterSprintingProxy proxy;
	
	public static BetterSprintingConfig config;

	public static final String modId = "bettersprinting";
	public static final String modVersion = "2.3.0";
	public static final String buildId = "2019-1";
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		Log.load();
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
	
	@NetworkCheckHandler
	public boolean onNetworkCheck(Map<String,String> versions, Side side){
		String version = versions.get(modId);
		return version == null || modVersion.equals(version);
	}
}
