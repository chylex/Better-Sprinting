package chylex.bettersprinting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import chylex.bettersprinting.system.Log;

@Mod(modid = BetterSprintingMod.modId,
     name = "Better Sprinting",
     useMetadata = true,
     guiFactory = "chylex.bettersprinting.client.gui.ModGuiFactory",
     acceptableRemoteVersions = "*",
     updateJSON = "https://raw.githubusercontent.com/chylex/Better-Sprinting/master/UpdateInfo.json")
public class BetterSprintingMod{
	@Instance(BetterSprintingMod.modId)
	public static BetterSprintingMod instance;
	
	@SidedProxy(clientSide = "chylex.bettersprinting.client.ClientProxy", serverSide = "chylex.bettersprinting.server.ServerProxy")
	public static BetterSprintingProxy proxy;
	
	public static BetterSprintingConfig config;
	
	public static final String modId = "bettersprinting";
	public static final String buildId = "26-09-2017-0";
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
