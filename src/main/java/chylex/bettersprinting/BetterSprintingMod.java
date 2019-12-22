package chylex.bettersprinting;
import chylex.bettersprinting.client.ClientProxy;
import chylex.bettersprinting.server.ServerProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BetterSprintingMod.id)
@EventBusSubscriber(modid = BetterSprintingMod.id, bus = Bus.MOD)
public final class BetterSprintingMod{
	public static final String id = "bettersprinting";
	public static final Logger log = LogManager.getLogger("BetterSprinting");
	
	public static final BetterSprintingProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	public static BetterSprintingConfig config;
	
	public BetterSprintingMod(){
		proxy.onConstructed(ModLoadingContext.get());
	}
	
	@SubscribeEvent
	public static void onConfigLoading(final ModConfig.Loading e){
		config = new BetterSprintingConfig(e.getConfig());
		config.migrate();
	}
	
	@SubscribeEvent
	public static void onLoadComplete(final FMLLoadCompleteEvent e){
		proxy.onLoaded(e);
	}
}
