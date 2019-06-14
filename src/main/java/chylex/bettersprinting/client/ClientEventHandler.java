package chylex.bettersprinting.client;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.KeyBindingList.CategoryEntry;
import net.minecraft.client.gui.widget.list.KeyBindingList.KeyEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
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
		Screen gui = e.getGui();
		
		if (gui instanceof ControlsScreen){
			ControlsScreen controls = (ControlsScreen)gui;
			
			controls.buttons.stream().filter(btn -> btn instanceof OptionButton && ((OptionButton)btn).field_146137_o == AbstractOption.field_216719_z).findFirst().ifPresent(btn -> {
				controls.buttons.remove(btn);
				controls.children().remove(btn);
			});
			
			controls.field_146494_r.children.removeIf(entry ->
				(entry instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entry).keybinding)) ||
				(entry instanceof CategoryEntry && ((CategoryEntry)entry).labelText.equals(I18n.format(ClientModManager.categoryName)))
			);
			
			if (!(controls.field_146496_h instanceof GuiSprint)){
				e.addWidget(new GuiButtonExt((controls.width / 2) + 5, 18, 150, 20, "Better Sprinting", __ -> {
					mc.displayGuiScreen(new GuiSprint(mc.field_71462_r));
				}));
			}
		}
	}
	
	private ClientEventHandler(){}
}
