package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingNetwork;
import chylex.bettersprinting.client.gui.GuiButton;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.gui.widget.list.KeyBindingList.CategoryEntry;
import net.minecraft.client.gui.widget.list.KeyBindingList.KeyEntry;
import net.minecraft.client.resources.I18n;
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
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler{
	private static final Minecraft mc = Minecraft.getInstance();
	private static boolean stopCheckingNewServer;
	
	public static boolean showDisableWarningWhenPossible;
	
	@SubscribeEvent
	public static void onPlayerLoginClient(final PlayerLoggedInEvent e){
		IntegrityCheck.register();
	}
	
	@SubscribeEvent
	public static void onPlayerJoinWorld(final EntityJoinWorldEvent e){
		if (stopCheckingNewServer || e.getEntity() != mc.player){
			return;
		}
		
		stopCheckingNewServer = true;
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod.get()){
			BetterSprintingNetwork.sendToServer(ClientNetwork.writeModNotification(10));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onGuiOpen(final GuiOpenEvent e){
		if (stopCheckingNewServer && mc.getRenderViewEntity() == null){
			ClientModManager.onDisconnectedFromServer();
			LivingUpdate.cleanup();
			IntegrityCheck.unregister();
			stopCheckingNewServer = false;
			showDisableWarningWhenPossible = false;
		}
	}
	
	@SubscribeEvent
	public static void onClientTick(final ClientTickEvent e){
		if (e.phase != Phase.END || mc.player == null){
			return;
		}
		
		if (showDisableWarningWhenPossible){
			ClientModManager.showChatMessage(I18n.format(ClientModManager.svDisableMod ? "bs.game.disabled" : "bs.game.reenabled"));
			showDisableWarningWhenPossible = false;
		}
		
		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
	}
	
	@SubscribeEvent
	public static void onGuiInitPost(final GuiScreenEvent.InitGuiEvent.Post e){
		final Screen gui = e.getGui();
		
		if (gui instanceof ControlsScreen){
			final ControlsScreen controls = (ControlsScreen)gui;
			
			e.getWidgetList()
			 .stream()
			 .filter(btn -> btn instanceof OptionButton && ((OptionButton)btn).enumOptions == AbstractOption.AUTO_JUMP)
			 .findFirst()
			 .ifPresent(e::removeWidget);
			
			controls.getEventListeners()
			        .stream()
			        .filter(widget -> widget instanceof KeyBindingList)
			        .map(widget -> ((KeyBindingList)widget).getEventListeners())
			        .findFirst()
			        .ifPresent(children -> children.removeIf(entry ->
			        	(entry instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entry).keybinding)) ||
			        	(entry instanceof CategoryEntry && ((CategoryEntry)entry).labelText.equals(ClientModManager.keyCategory))
			        ));
			
			if (!GuiSprint.openedControlsFromSprintMenu){
				e.addWidget(new GuiButton((controls.width / 2) + 5, 18, 150, "Better Sprinting", () -> mc.displayGuiScreen(new GuiSprint(mc.currentScreen))));
			}
		}
	}
	
	private ClientEventHandler(){}
}
