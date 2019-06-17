package chylex.bettersprinting.client;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiKeyBindingList.CategoryEntry;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.commons.lang3.ArrayUtils;
import chylex.bettersprinting.client.compatibility.OldNotificationPacket;
import chylex.bettersprinting.client.gui.GuiButtonInteractive;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
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
	
	private final Minecraft mc = Minecraft.getMinecraft();
	private boolean stopChecking;
	
	@SubscribeEvent
	public void onPlayerLoginClient(PlayerLoggedInEvent e){
		IntegrityCheck.register();
		UpdateNotificationManager.run();
	}
	
	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopChecking || e.entity != mc.thePlayer){
			return;
		}
		
		stopChecking = true;
		
		if (!mc.isIntegratedServerRunning() && mc.func_147104_D() != null && !ClientSettings.disableMod){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
			OldNotificationPacket.sendServerNotification(mc.thePlayer.sendQueue);
		}
	}
	
	@SubscribeEvent
	public void onClientDisconnectedFromServer(ClientDisconnectionFromServerEvent e){
		ClientModManager.onDisconnectedFromServer();
		IntegrityCheck.unregister();
		LivingUpdate.cleanup();
		stopChecking = false;
	}
	
	@SubscribeEvent
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post e){
		GuiScreen gui = e.gui;
		
		if (gui instanceof GuiControls){
			GuiControls controls = (GuiControls)gui;
			
			GuiKeyBindingList keyList = controls.keyBindingList;
			IGuiListEntry[] entries = keyList.field_148190_m;
			
			int[] keyIndices = IntStream
				.range(0, entries.length)
				.filter(index -> (entries[index] instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entries[index]).field_148282_b)) ||
				                 (entries[index] instanceof CategoryEntry && ((CategoryEntry)entries[index]).field_148285_b.equals(I18n.format(ClientModManager.categoryName))))
				.toArray();
			
			keyList.field_148190_m = ArrayUtils.removeAll(keyList.field_148190_m, keyIndices);
			
			if (!(controls.parentScreen instanceof GuiSprint)){
				e.buttonList.add(0, new GuiButtonInteractive(205, (controls.width / 2) + 5, 42, 150, 20, "Better Sprinting", __ -> {
					mc.displayGuiScreen(new GuiSprint(mc.currentScreen));
				}));
			}
		}
	}
	
	private ClientEventHandler(){}
}
