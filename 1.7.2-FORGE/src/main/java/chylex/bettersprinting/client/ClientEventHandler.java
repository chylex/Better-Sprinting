package chylex.bettersprinting.client;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.gui.GuiControlsCustom;
import chylex.bettersprinting.client.update.UpdateThread;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class ClientEventHandler{
	private long lastNotificationTime = -1;
	
	@SubscribeEvent
	public void onPlayerJoinWorld(PlayerLoggedInEvent e){
		if (ClientModManager.enableUpdateNotifications){
			long time=System.currentTimeMillis();
			
			if (lastNotificationTime==-1||time-lastNotificationTime>1200000){
				lastNotificationTime=time;
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui instanceof GuiControls&&!ClientModManager.fromBs)e.gui=new GuiControlsCustom((GuiControls)e.gui);
	}
}
