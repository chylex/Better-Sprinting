package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.compatibility.OldNotificationPacket;
import chylex.bettersprinting.client.gui.GuiControlsCustom;
import chylex.bettersprinting.client.update.UpdateThread;
import chylex.bettersprinting.system.PacketPipeline;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public void onPlayerLoginClient(PlayerLoggedInEvent e){
		if (ClientSettings.enableUpdateNotifications || ClientSettings.enableBuildCheck){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 1200000){
				lastNotificationTime = time;
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopChecking || e.entity != Minecraft.getMinecraft().thePlayer)return;
		
		stopChecking = true;
		Minecraft mc = Minecraft.getMinecraft();
		
		if (!mc.isIntegratedServerRunning() && mc.func_147104_D() != null && !ClientSettings.disableMod){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
			OldNotificationPacket.sendServerNotification(mc.thePlayer.sendQueue);
		}
	}
	
	@SubscribeEvent
	public void onClientDisconnectedFromServer(ClientDisconnectionFromServerEvent e){
		ClientModManager.svSurvivalFlyingBoost = ClientModManager.svRunInAllDirs = ClientModManager.svDisableMod = false;
		stopChecking = false;
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui != null && e.gui.getClass() == GuiControls.class)e.gui = new GuiControlsCustom((GuiControls)e.gui);
	}
	
	private ClientEventHandler(){}
}
