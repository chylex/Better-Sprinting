package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.ArrayUtils;

public class ClientProxy extends BetterSprintingProxy{
	@Override
	public String getMinecraftVersion(){
		return RealmsSharedConstants.VERSION_STRING;
	}
	
	@Override
	public void onConstructed(ModLoadingContext ctx){
		ClientSettings.register(ctx);
		ClientEventHandler.register();
		PacketPipeline.initialize(new ClientNetwork());
	}
	
	@Override
	public void onLoaded(FMLLoadCompleteEvent e){
		Minecraft mc = Minecraft.getInstance();
		
		mc.addScheduledTask(() -> {
			GameSettings settings = mc.gameSettings;
			
			settings.keyBindings = ArrayUtils.addAll(settings.keyBindings, new KeyBinding[]{
				ClientModManager.keyBindSprintToggle,
				ClientModManager.keyBindSneakToggle,
				ClientModManager.keyBindOptionsMenu,
			});
			
			ClientSettings.updateKeyBindings();
		});
	}
}
