package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.ClientModManager;

@SideOnly(Side.CLIENT)
public final class LogicImplOverride{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new LogicImplOverride());
	}
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private byte checkTimer = -120;
	private boolean stopChecking = false;
	
	private LogicImplOverride(){}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.getGui() != null && e.getGui().getClass() == GuiDownloadTerrain.class){
			// TODO
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase == Phase.END || mc.player == null || mc.playerController == null)return;
		
		if (!stopChecking && --checkTimer < -125){
			checkTimer = 120;
			Class<?> controllerClass = mc.playerController.getClass();
			
			if (false){ // TODO
				mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format("bs.game.integrity").replace("$", controllerClass.getName())));
				stopChecking = true;
			}
		}
	}
}
