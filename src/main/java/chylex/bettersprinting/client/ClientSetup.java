package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingNetwork;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.ArrayUtils;

public final class ClientSetup{
	public static void setup(){
		BetterSprintingConfig.initialize(ModConfig.Type.CLIENT, ClientSettings.getSpec(), "client");
		BetterSprintingNetwork.initialize(new ClientNetwork());
		
		MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ClientSetup.class);
	}
	
	@SubscribeEvent
	public static void onLoadComplete(final FMLLoadCompleteEvent e){
		Minecraft mc = Minecraft.getInstance();
		
		mc.execute(() -> {
			GameSettings settings = mc.gameSettings;
			
			settings.keyBindings = ArrayUtils.addAll(settings.keyBindings,
				ClientModManager.keyBindSprintToggle,
				ClientModManager.keyBindSneakToggle,
				ClientModManager.keyBindOptionsMenu
			);
			
			if (BetterSprintingConfig.isNew()){
				ClientSettings.firstTimeSetup(settings);
			}
			
			// this should work whether it's called before or after Forge's post-load GameSettings.loadOptions call
			ClientSettings.keyInfoSprintHold.writeInto(ClientModManager.keyBindSprintHold);
			ClientSettings.keyInfoSprintToggle.writeInto(ClientModManager.keyBindSprintToggle);
			ClientSettings.keyInfoSneakToggle.writeInto(ClientModManager.keyBindSneakToggle);
			ClientSettings.keyInfoOptionsMenu.writeInto(ClientModManager.keyBindOptionsMenu);
			KeyBinding.resetKeyBindingArrayAndHash();
		});
	}
}
