package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.gui.GuiButton;
import chylex.bettersprinting.client.gui.GuiSprint;
import chylex.bettersprinting.client.player.IntegrityCheck;
import chylex.bettersprinting.client.player.LivingUpdate;
import chylex.bettersprinting.client.update.UpdateNotificationManager;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiKeyBindingList.CategoryEntry;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = BetterSprintingMod.modId)
public final class ClientEventHandler{
	private static final Minecraft mc = Minecraft.getMinecraft();
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
		
		if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientSettings.disableMod){
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
			mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix + I18n.format(ClientModManager.svDisableMod ? "bs.game.disabled" : "bs.game.reenabled")));
			showDisableWarningWhenPossible = false;
		}
		
		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
	}
	
	@SubscribeEvent
	public static void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post e){
		GuiScreen gui = e.getGui();
		
		if (gui instanceof GuiControls){
			GuiControls controls = (GuiControls)gui;
			GuiKeyBindingList list = controls.keyBindingList;
			
			e.getButtonList()
			 .stream()
			 .filter(btn -> btn instanceof GuiOptionButton && btn.id == Options.AUTO_JUMP.getOrdinal())
			 .findFirst()
			 .ifPresent(e.getButtonList()::remove);
			
			list.listEntries = ArrayUtils.removeElements(list.listEntries, Arrays.stream(list.listEntries).filter(entry ->
				(entry instanceof KeyEntry && ArrayUtils.contains(ClientModManager.keyBindings, ((KeyEntry)entry).keybinding)) ||
				(entry instanceof CategoryEntry && ((CategoryEntry)entry).labelText.equals(I18n.format(ClientModManager.categoryName)))
			).toArray(IGuiListEntry[]::new));
			
			if (!(controls.parentScreen instanceof GuiSprint)){
				e.getButtonList().add(new GuiButton(6969, (controls.width / 2) + 5, 42, 150, "Better Sprinting", () -> mc.displayGuiScreen(new GuiSprint(mc.currentScreen))));
			}
		}
	}
	
	private ClientEventHandler(){}
}
