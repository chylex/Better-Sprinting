package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.tuple.Triple;
import java.util.List;

public final class ServerProxy extends BetterSprintingProxy{
	@Override
	public void onConstructed(ModLoadingContext ctx){
		ServerSettings.register(ctx);
		PacketPipeline.initialize(new ServerNetwork());
	}

	@Override
	public void onLoaded(FMLLoadCompleteEvent e){}
	
	@Override
	public void migrateConfig(List<Triple<Character, String, String>> oldConfig){
		for(Triple<Character, String, String> entry:oldConfig){
			if (entry.getLeft() == 'B'){
				BooleanValue value;
				
				switch(entry.getMiddle()){
					case "disableClientMod":       value = ServerSettings.disableClientMod; break;
					case "enableSurvivalFlyBoost": value = ServerSettings.enableSurvivalFlyBoost; break;
					case "enableAllDirs":          value = ServerSettings.enableAllDirs; break;
					default: continue;
				}
				
				BetterSprintingMod.config.set(value, entry.getRight().equalsIgnoreCase("true"));
			}
		}
	}
}
