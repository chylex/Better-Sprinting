package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class LogicImplOverride{
	public static void register(){
		LogicImplOverride instance = new LogicImplOverride();
		FMLCommonHandler.instance().bus().register(instance);
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	private LogicImplOverride(){}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui.getClass() == GuiDownloadTerrain.class){
			Minecraft mc = Minecraft.getMinecraft();
			mc.playerController = new PlayerControllerMPOverride(mc,(NetHandlerPlayClient)FMLClientHandler.instance().getClientPlayHandler());
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoinWorld(PlayerLoggedInEvent e){
		Class<?> controllerClass = Minecraft.getMinecraft().playerController.getClass();
		
		if (controllerClass != PlayerControllerMPOverride.class){
			e.player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"[Better Sprinting]"+EnumChatFormatting.RESET+" Integrity verification failed, another mod is conflicting with Better Sprinting. Try installing PlayerAPI to resolve the conflict. Conflicting class: "+controllerClass.getName()));
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
