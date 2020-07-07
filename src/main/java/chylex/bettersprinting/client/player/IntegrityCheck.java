package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class IntegrityCheck{
	static boolean isValidated = false;
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void unregister(){
		MinecraftForge.EVENT_BUS.unregister(instance);
		isValidated = false;
	}

	private static final IntegrityCheck instance = new IntegrityCheck();
	private static final Minecraft mc = Minecraft.getInstance();
	
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent e){
		if (e.phase == Phase.END && mc.player != null && mc.player.ticksExisted > 1){
			if (!isValidated){
				ClientModManager.showChatMessage(I18n.format("bs.game.integrity"));
			}
			
			unregister();
		}
	}
}
