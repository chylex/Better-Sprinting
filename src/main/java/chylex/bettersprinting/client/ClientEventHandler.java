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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import chylex.bettersprinting.client.gui.GuiButtonInteractive;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
import chylex.bettersprinting.system.PacketPipeline;

@SideOnly(Side.CLIENT)
public final class ClientEventHandler{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
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
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
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
			IGuiListEntry[] entries = keyList.listEntries;
			
			int[] keyIndices = IntStream
				.range(0, entries.length)
				.filter(index -> (entries[index] instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entries[index]).keybinding)) ||
				                 (entries[index] instanceof CategoryEntry && ((CategoryEntry)entries[index]).labelText.equals(I18n.format(ClientModManager.categoryName))))
				.toArray();
			
			keyList.listEntries = ArrayUtils.removeAll(keyList.listEntries, keyIndices);
			
			if (!(controls.parentScreen instanceof GuiSprint)){
				e.buttonList.add(0, new GuiButtonInteractive(205, (controls.width / 2) + 5, 42, 150, 20, "Better Sprinting", __ -> {
					mc.displayGuiScreen(new GuiSprint(mc.currentScreen));
				}));
			}
		}
	}
	
	private ClientEventHandler(){}
}
