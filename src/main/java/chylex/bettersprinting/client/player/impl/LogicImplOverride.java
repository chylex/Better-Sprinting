package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class LogicImplOverride{
	public static void register(){
		LogicImplOverride instance = new LogicImplOverride();
		FMLCommonHandler.instance().bus().register(instance);
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	private byte checkTimer = -120;
	private boolean stopChecking = false;
	
	private LogicImplOverride(){}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui != null && e.gui.getClass() == GuiDownloadTerrain.class && Minecraft.getMinecraft().playerController.getClass() != PlayerControllerMPOverride.class){
			Minecraft mc = Minecraft.getMinecraft();
			mc.playerController = new PlayerControllerMPOverride(mc,(NetHandlerPlayClient)FMLClientHandler.instance().getClientPlayHandler());
			
			EntityPlayerSP prevPlayer = mc.thePlayer;
			mc.theWorld.removeEntity(prevPlayer);
			
			mc.thePlayer = mc.playerController.func_178892_a(prevPlayer.worldObj,prevPlayer.getStatFileWriter());
			mc.playerController.flipPlayer(mc.thePlayer);
			mc.thePlayer.preparePlayerToSpawn();
			mc.theWorld.spawnEntityInWorld(mc.thePlayer);
            mc.thePlayer.movementInput = new MovementInputFromOptions(mc.gameSettings);
            mc.playerController.setPlayerCapabilities(mc.thePlayer);
            mc.setRenderViewEntity(mc.thePlayer);
			mc.thePlayer.dimension = prevPlayer.dimension;
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase == Phase.END || Minecraft.getMinecraft().thePlayer == null)return;
		
		if (!stopChecking && --checkTimer < -125){
			checkTimer = 120;
			Minecraft mc = Minecraft.getMinecraft();
			Class<?> controllerClass = mc.playerController.getClass();
			
			if (controllerClass != PlayerControllerMPOverride.class){
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"[Better Sprinting]"+EnumChatFormatting.RESET+" Integrity verification failed, another mod is conflicting with Better Sprinting. Try installing PlayerAPI to resolve the conflict. Conflicting class: "+controllerClass.getName()));
				stopChecking = true;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static final class PlayerControllerMPOverride extends PlayerControllerMP{
		private final Minecraft mc;
		private final NetHandlerPlayClient netHandler;
		
		public PlayerControllerMPOverride(Minecraft mc, NetHandlerPlayClient netHandler){
			super(mc,netHandler);
			this.mc = mc;
			this.netHandler = netHandler;
		}
		
		@Override
		public EntityPlayerSP func_178892_a(World world, StatFileWriter statWriter){
			return new PlayerOverride(mc,world,netHandler,statWriter);
		}
	}
}
