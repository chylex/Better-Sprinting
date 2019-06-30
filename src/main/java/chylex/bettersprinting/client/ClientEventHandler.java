package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.gui.widget.list.KeyBindingList.CategoryEntry;
import net.minecraft.client.gui.widget.list.KeyBindingList.KeyEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = BetterSprintingMod.modId)
public final class ClientEventHandler{
	private static final Minecraft mc = Minecraft.getInstance();
	private static boolean stopChecking;
	
	@SubscribeEvent
	public static void onPlayerLoginClient(PlayerLoggedInEvent e){
		IntegrityCheck.register();
		UpdateNotificationManager.run();
	}
	
	@SubscribeEvent
	public static void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopChecking || e.getEntity() != mc.player){
			return;
		}
		
		stopChecking = true;
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod.get()){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onGuiOpen(GuiOpenEvent e){
		if (stopChecking && mc.getRenderViewEntity() == null){
			ClientModManager.onDisconnectedFromServer();
			IntegrityCheck.unregister();
			LivingUpdate.cleanup();
			stopChecking = false;
		}
	}
	
	@SubscribeEvent
	public static void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post e){
		Screen gui = e.getGui();
		
		if (gui instanceof ControlsScreen){
			ControlsScreen controls = (ControlsScreen)gui;
			
			e.getWidgetList()
			 .stream()
			 .filter(btn -> btn instanceof OptionButton && ((OptionButton)btn).enumOptions == AbstractOption.AUTO_JUMP)
			 .findFirst()
			 .ifPresent(e::removeWidget);
			
			controls.children()
			        .stream()
			        .filter(widget -> widget instanceof KeyBindingList)
			        .map(widget -> ((KeyBindingList)widget).children())
			        .findFirst()
			        .ifPresent(children -> children.removeIf(entry ->
			        	(entry instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entry).keybinding)) ||
			        	(entry instanceof CategoryEntry && ((CategoryEntry)entry).labelText.equals(I18n.format(ClientModManager.categoryName)))
			        ));
			
			if (!(controls.parentScreen instanceof GuiSprint)){
				e.addWidget(new GuiButtonExt((controls.width / 2) + 5, 18, 150, 20, "Better Sprinting", __ -> {
					mc.displayGuiScreen(new GuiSprint(mc.currentScreen));
				}));
			}
		}
	}
	
	private ClientEventHandler(){}
}
