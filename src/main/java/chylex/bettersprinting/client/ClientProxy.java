package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import java.util.List;

public class ClientProxy extends BetterSprintingProxy{
	@Override
	public void onConstructed(ModLoadingContext ctx){
		ClientSettings.register(ctx);
		PacketPipeline.initialize(new ClientNetwork());
	}
	
	@Override
	public void onLoaded(FMLLoadCompleteEvent e){
		Minecraft mc = Minecraft.getInstance();
		
		mc.execute(() -> {
			GameSettings settings = mc.gameSettings;
			
			settings.keyBindings = ArrayUtils.addAll(settings.keyBindings,
				ClientModManager.keyBindSprintToggle,
				ClientModManager.keyBindSneakToggle,
				ClientModManager.keyBindOptionsMenu
			);
			
			ClientSettings.updateKeyBindings(); // UPDATE | Ensure this doesn't override vanilla sprint key on start | 1.14.3
		});
	}
	
	@Override
	public void migrateConfig(List<Triple<Character, String, String>> oldConfig){
		for(Triple<Character, String, String> entry:oldConfig){
			if (entry.getLeft() == 'B'){
				BooleanValue value;
				
				switch(entry.getMiddle()){
					case "disableMod":                value = ClientSettings.disableMod; break;
					case "enableDoubleTap":           value = ClientSettings.enableDoubleTap; break;
					case "enableAllDirs":             value = ClientSettings.enableAllDirs; break;
					case "flyOnGround":               value = ClientSettings.flyOnGround; break;
					case "enableUpdateNotifications": value = ClientSettings.enableUpdateNotifications; break;
					case "enableBuildCheck":          value = ClientSettings.enableBuildCheck; break;
					default: continue;
				}
				
				BetterSprintingMod.config.set(value, entry.getRight().equalsIgnoreCase("true"));
			}
			else if (entry.getLeft() == 'I'){
				IntValue value;
				
				switch(entry.getMiddle()){
					case "flySpeedBoost": value = ClientSettings.flySpeedBoost; break;
					default: continue;
				}
				
				BetterSprintingMod.config.set(value, Integer.parseInt(entry.getRight()));
			}
		}
	}
}
