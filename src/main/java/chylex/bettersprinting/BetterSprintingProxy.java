package chylex.bettersprinting;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.tuple.Triple;
import java.util.List;

public abstract class BetterSprintingProxy{
	public abstract void onConstructed(ModLoadingContext ctx);
	public abstract void onLoaded(FMLLoadCompleteEvent e);
	public abstract void migrateConfig(List<Triple<Character, String, String>> oldConfig);
}
