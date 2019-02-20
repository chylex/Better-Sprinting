package chylex.bettersprinting.client;
import chylex.bettersprinting.client.gui.GuiButtonCustom;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.GameSettings.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList.CategoryEntry;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
final class ClientEventHandler{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	private final Minecraft mc = Minecraft.getInstance();
	private boolean stopChecking;
	
	@SubscribeEvent
	public void onPlayerLoginClient(PlayerLoggedInEvent e){
		IntegrityCheck.register();
		UpdateNotificationManager.run();
	}
	
	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopChecking || e.getEntity() != mc.player){
			return;
		}
		
		stopChecking = true;
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod.get()){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onGuiOpen(GuiOpenEvent e){
		if (stopChecking && mc.getRenderViewEntity() == null){
			ClientModManager.onDisconnectedFromServer();
			IntegrityCheck.unregister();
			LivingUpdate.cleanup();
			stopChecking = false;
		}
	}
	
	@SubscribeEvent
	public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post e){
		GuiScreen gui = e.getGui();
		
		if (gui instanceof GuiControls){
			GuiControls controls = (GuiControls)gui;
			
			controls.buttons.stream().filter(btn -> btn.id == Options.AUTO_JUMP.getOrdinal()).findFirst().ifPresent(btn -> {
				controls.buttons.remove(btn);
				controls.getChildren().remove(btn);
			});
			
			controls.keyBindingList.entries.removeIf(entry ->
				(entry instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entry).keybinding)) ||
				(entry instanceof CategoryEntry && ((CategoryEntry)entry).labelText.equals(I18n.format(ClientModManager.categoryName)))
			);
			
			if (!(controls.parentScreen instanceof GuiSprint)){
				e.addButton(new GuiButtonCustom(205, (controls.width / 2) + 5, 42, 150, 20, "Better Sprinting", __ -> {
					mc.displayGuiScreen(new GuiSprint(mc.currentScreen));
				}));
			}
		}
	}
	
	private ClientEventHandler(){}
}
