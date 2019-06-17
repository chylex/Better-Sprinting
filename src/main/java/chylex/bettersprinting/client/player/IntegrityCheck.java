package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import chylex.bettersprinting.client.ClientModManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class IntegrityCheck{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void unregister(){
		MinecraftForge.EVENT_BUS.unregister(instance);
	}

	private static final IntegrityCheck instance = new IntegrityCheck();
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private IntegrityCheck(){}
	
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent e){
		if (e.phase == Phase.END && mc.thePlayer != null && mc.thePlayer.ticksExisted > 1){
			if (!LivingUpdate.checkIntegrity()){
				mc.thePlayer.addChatMessage(new ChatComponentText(ClientModManager.chatPrefix + I18n.format("bs.game.integrity")));
			}
			
			unregister();
		}
	}
}
