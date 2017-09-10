package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.commons.lang3.ArrayUtils;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.client.player.impl.CompatPlayerAPI;
import chylex.bettersprinting.system.Log;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.core.BetterSprintingCore;

public class ClientProxy extends BetterSprintingProxy{
	@Override
	public void loadSidedConfig(BetterSprintingConfig config){
		ClientSettings.reload(config);
	}
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e){
		if (!BetterSprintingCore.wasInitialized()){ // Forge fucks with 'acceptedMinecraftVersions', so no, I'm not going to use that, thank you very much
			throw new RuntimeException("This version of Better Sprinting only supports Minecraft "+BetterSprintingCore.supportedMinecraftVersion);
		}
		
		Log.initializeDebug();
		ClientEventHandler.register();
		PacketPipeline.initialize(new ClientNetwork());
	}
	
	@Override
	public void onInit(FMLInitializationEvent e){
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		
		settings.keyBindings = ArrayUtils.addAll(settings.keyBindings, new KeyBinding[]{
			ClientModManager.keyBindSprintToggle,
			ClientModManager.keyBindSneakToggle,
			ClientModManager.keyBindOptionsMenu,
		});
		
		KeyBinding.resetKeyBindingArrayAndHash();
		
		if (BetterSprintingCore.usePlayerAPI()){
			CompatPlayerAPI.register();
		}
	}
	
	@Override
	public void onServerStarting(FMLServerStartingEvent e){}
}
