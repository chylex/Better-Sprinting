package chylex.bettersprinting.client;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.gui.GuiControlsCustom;
import chylex.bettersprinting.client.update.UpdateThread;

public final class ClientEventHandler{
	public static void register(){
		ClientEventHandler events = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(events);
		FMLCommonHandler.instance().bus().register(events);
	}
	
	private long lastNotificationTime = -1;
	
	@SubscribeEvent
	public void onPlayerJoinWorld(PlayerLoggedInEvent e){
		if (ClientSettings.enableUpdateNotifications || ClientSettings.enableBuildCheck){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 1200000){
				lastNotificationTime = time;
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui instanceof GuiControls && !ClientModManager.fromBs)e.gui = new GuiControlsCustom((GuiControls)e.gui);
	}
	
	private ClientEventHandler(){}
}
