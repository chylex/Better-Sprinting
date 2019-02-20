package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@OnlyIn(Dist.CLIENT)
public final class IntegrityCheck{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void unregister(){
		MinecraftForge.EVENT_BUS.unregister(instance);
	}

	private static final IntegrityCheck instance = new IntegrityCheck();
	private static final Minecraft mc = Minecraft.getInstance();
	
	private IntegrityCheck(){}
	
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent e){
		if (e.phase == Phase.END && mc.player != null && mc.player.ticksExisted > 1){
			if (!LivingUpdate.checkIntegrity()){
				mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix + I18n.format("bs.game.integrity")));
			}
			
			unregister();
		}
	}
}
