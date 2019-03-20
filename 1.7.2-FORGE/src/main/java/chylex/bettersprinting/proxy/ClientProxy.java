package chylex.bettersprinting.proxy;
import api.player.client.ClientPlayerAPI;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import chylex.bettersprinting.client.ClientEventHandler;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.player.PlayerBase;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerEvents(FMLPreInitializationEvent e){
		ClientEventHandler events=new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(events);
		FMLCommonHandler.instance().bus().register(events);
		
		ClientModManager.loadSprint(Minecraft.getMinecraft());
	}
	
	@Override
	public void initMod(){
		GameSettings settings=Minecraft.getMinecraft().gameSettings;
		KeyBinding[] newBinds=new KeyBinding[settings.keyBindings.length-1];
		for(int a=0,index=0; a<settings.keyBindings.length; a++){
			if (settings.keyBindings[a]!=settings.keyBindSprint)newBinds[index++]=settings.keyBindings[a];
		}
		settings.keyBindings=newBinds;
		settings.keyBindSprint.setKeyCode(0);
		KeyBinding.resetKeyBindingArrayAndHash();
		
		ClientPlayerAPI.register("BetterSprinting",PlayerBase.class);
	}
}
