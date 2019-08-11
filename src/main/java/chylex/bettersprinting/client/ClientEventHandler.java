package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.gui.GuiButton;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = BetterSprintingMod.modId)
public final class ClientEventHandler{
	private static final Minecraft mc = Minecraft.getInstance();
	private static boolean stopCheckingNewServer;
	
	public static boolean showDisableWarningWhenPossible;
	
	@SubscribeEvent
	public static void onPlayerLoginClient(PlayerLoggedInEvent e){
		IntegrityCheck.register();
		UpdateNotificationManager.run();
	}
	
	@SubscribeEvent
	public static void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if (stopCheckingNewServer || e.getEntity() != mc.player){
			return;
		}
		
		stopCheckingNewServer = true;
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod.get()){
			PacketPipeline.sendToServer(ClientNetwork.writeModNotification(10));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onGuiOpen(GuiOpenEvent e){
		if (stopCheckingNewServer && mc.getRenderViewEntity() == null){
			ClientModManager.onDisconnectedFromServer();
			IntegrityCheck.unregister();
			LivingUpdate.cleanup();
			stopCheckingNewServer = false;
			showDisableWarningWhenPossible = false;
		}
	}
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent e){
		if (e.phase != Phase.END || mc.player == null){
			return;
		}
		
		if (showDisableWarningWhenPossible){
			mc.player.sendMessage(new StringTextComponent(ClientModManager.chatPrefix + I18n.format(ClientModManager.svDisableMod ? "bs.game.disabled" : "bs.game.reenabled")));
			showDisableWarningWhenPossible = false;
		}
		
		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
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
				e.addWidget(new GuiButton((controls.width / 2) + 5, 18, 150, "Better Sprinting", () -> mc.displayGuiScreen(new GuiSprint(mc.currentScreen))));
			}
		}
	}
	
	private ClientEventHandler(){}
}
