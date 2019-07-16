package chylex.bettersprinting;
import chylex.bettersprinting.client.ClientProxy;
import chylex.bettersprinting.server.ServerProxy;
import chylex.bettersprinting.system.Log;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetterSprintingMod.modId)
public class BetterSprintingMod{
	public static final BetterSprintingProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public static BetterSprintingConfig config;
	
	public static final String modId = "bettersprinting";
	public static final String buildId = "2019-3";
	public static String modVersion;
	
	public BetterSprintingMod(){
		ModLoadingContext ctx = ModLoadingContext.get();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modVersion = ctx.getActiveContainer().getModInfo().getVersion().toString();
		proxy.onConstructed(ctx);
		
		bus.register(this);
		Log.load();
	}
	
	@SubscribeEvent
	public void onConfigLoading(final ModConfig.Loading e){
		config = new BetterSprintingConfig(e.getConfig());
		config.migrate();
	}
	
	@SubscribeEvent
	public void onLoadComplete(final FMLLoadCompleteEvent e){
		proxy.onLoaded(e);
	}
}
