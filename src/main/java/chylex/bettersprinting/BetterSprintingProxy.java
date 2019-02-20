package chylex.bettersprinting;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public abstract class BetterSprintingProxy{
	public abstract String getMinecraftVersion();
	public abstract void onConstructed(ModLoadingContext ctx);
	public abstract void onLoaded(FMLLoadCompleteEvent e);
}
