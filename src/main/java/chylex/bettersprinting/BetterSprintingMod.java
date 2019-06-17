package chylex.bettersprinting;
import java.util.Map;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import chylex.bettersprinting.system.Log;

@Mod(modid = BetterSprintingMod.modId,
     version = BetterSprintingMod.modVersion,
     name = "Better Sprinting",
     useMetadata = true,
     guiFactory = "chylex.bettersprinting.client.gui.ModGuiFactory",
     acceptableRemoteVersions = "*",
     updateJSON = "https://raw.githubusercontent.com/chylex/Better-Sprinting/master/UpdateInfo.json")
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
