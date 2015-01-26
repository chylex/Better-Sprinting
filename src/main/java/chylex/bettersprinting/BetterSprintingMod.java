package chylex.bettersprinting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="BetterSprinting", name="Better Sprinting", useMetadata = true)
public class BetterSprintingMod{
	@Instance("BetterSprinting")
	public static BetterSprintingMod instance;
	
	@SidedProxy(clientSide="chylex.bettersprinting.client.ClientProxy", serverSide="chylex.bettersprinting.server.ServerProxy")
	public static BetterSprintingProxy proxy;
	
	public static final String buildId = "";
	public static String modVersion;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		modVersion = e.getModMetadata().version;
		proxy.onPreInit(e);
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		proxy.onInit(e);
	}
}
