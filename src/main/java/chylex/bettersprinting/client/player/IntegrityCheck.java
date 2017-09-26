package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.ClientModManager;

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
		if (e.phase == Phase.END && mc.player != null && mc.player.ticksExisted > 1){
			if (!LivingUpdate.checkIntegrity()){
				mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format("bs.game.integrity")));
			}
			
			unregister();
		}
	}
}
