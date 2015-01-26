package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.ArrayUtils;
import api.player.client.ClientPlayerAPI;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.client.player.PlayerBase;

public class ClientProxy extends BetterSprintingProxy{
	@Override
	public void onPreInit(FMLPreInitializationEvent e){
		ClientEventHandler.register();
		ClientModManager.loadSprint(Minecraft.getMinecraft());
	}
	
	@Override
	public void onInit(FMLInitializationEvent e){
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		settings.keyBindings = ArrayUtils.removeElement(settings.keyBindings,settings.keyBindSprint);
		settings.keyBindSprint.setKeyCode(0);
		KeyBinding.resetKeyBindingArrayAndHash();
		
		ClientPlayerAPI.register("BetterSprinting",PlayerBase.class);
	}
}
