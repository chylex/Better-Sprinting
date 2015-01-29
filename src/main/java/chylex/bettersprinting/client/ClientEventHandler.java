package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.compatibility.OldNotificationPacket;
import chylex.bettersprinting.client.gui.GuiControlsCustom;
import chylex.bettersprinting.client.update.UpdateThread;
import chylex.bettersprinting.system.PacketPipeline;

@SideOnly(Side.CLIENT)
public final class ClientEventHandler{
	public static void register(){
		ClientEventHandler events = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(events);
		FMLCommonHandler.instance().bus().register(events);
	}
	
	private long lastNotificationTime = -1;
	private boolean stopChecking;
	
	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopChecking || e.entity != Minecraft.getMinecraft().thePlayer)return;
		
		stopChecking = true;
		
		if (ClientSettings.enableUpdateNotifications || ClientSettings.enableBuildCheck){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 1200000){
				lastNotificationTime = time;
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod){
			ClientModManager.svSurvivalFlyingBoost = ClientModManager.svRunInAllDirs = ClientModManager.svDisableMod = false;
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
			OldNotificationPacket.sendServerNotification(mc.thePlayer.sendQueue);
		}
	}
	
	@SubscribeEvent
	public void onClientDisconnectedFromServer(ClientDisconnectionFromServerEvent e){
		stopChecking = ClientModManager.svSurvivalFlyingBoost = ClientModManager.svRunInAllDirs = ClientModManager.svDisableMod = false;
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui != null && e.gui.getClass() == GuiControls.class)e.gui = new GuiControlsCustom((GuiControls)e.gui);
	}
	
	private ClientEventHandler(){}
}
