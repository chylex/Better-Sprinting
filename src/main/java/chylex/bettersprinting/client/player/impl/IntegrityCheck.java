package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.player.LivingUpdate;

@SideOnly(Side.CLIENT)
public final class IntegrityCheck{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void unregister(){
		MinecraftForge.EVENT_BUS.unregister(instance);
		instance.initialPlayerPos = Vec3d.ZERO;
	}

	private static final IntegrityCheck instance = new IntegrityCheck();
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private Vec3d initialPlayerPos = Vec3d.ZERO;
	
	private IntegrityCheck(){}
	
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent e){
		if (e.phase == Phase.END && mc.player != null && mc.playerController != null){
			if (LivingUpdate.checkIntegrity()){
				unregister();
			}
			else if (initialPlayerPos.equals(Vec3d.ZERO)){
				initialPlayerPos = getPlayerPosXZ(mc.player);
			}
			else if (getPlayerPosXZ(mc.player).squareDistanceTo(initialPlayerPos) > 1D){
				mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format("bs.game.integrity")));
				unregister();
			}
		}
	}
	
	private static Vec3d getPlayerPosXZ(EntityPlayer player){
		Vec3d vec = player.getPositionVector();
		return new Vec3d(vec.x, 0D, vec.z);
	}
}
