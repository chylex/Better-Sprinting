package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
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
		if (e.getGui() != null && e.getGui().getClass() == GuiDownloadTerrain.class && Minecraft.getMinecraft().playerController.getClass() != PlayerControllerMPOverride.class){
			mc.playerController = new PlayerControllerMPOverride(mc, (NetHandlerPlayClient)FMLClientHandler.instance().getClientPlayHandler());
			
			// UPDATE | Minecraft.setDimensionAndSpawnPlayer | 1.11.2
			EntityPlayerSP prevPlayer = mc.player;
			mc.world.removeEntity(prevPlayer);
			
			mc.setRenderViewEntity(null);
			mc.player = mc.playerController.func_192830_a(prevPlayer.world, prevPlayer.getStatFileWriter(), prevPlayer.func_192035_E());
			mc.player.getDataManager().setEntryValues(prevPlayer.getDataManager().getAll());
			mc.player.dimension = prevPlayer.dimension;
			mc.setRenderViewEntity(mc.player);
			mc.player.preparePlayerToSpawn();
			mc.player.setServerBrand(prevPlayer.getServerBrand());
			mc.world.spawnEntity(mc.player);
			mc.playerController.flipPlayer(mc.player);
			mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
			mc.player.setEntityId(prevPlayer.getEntityId());
			mc.playerController.setPlayerCapabilities(mc.player);
			mc.player.setReducedDebug(prevPlayer.hasReducedDebug());
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase == Phase.END || mc.player == null || mc.playerController == null)return;
		
		if (!stopChecking && --checkTimer < -125){
			checkTimer = 120;
			Class<?> controllerClass = mc.playerController.getClass();
			
			if (controllerClass != PlayerControllerMPOverride.class){
				mc.player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format("bs.game.integrity").replace("$", controllerClass.getName())));
				stopChecking = true;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static final class PlayerControllerMPOverride extends PlayerControllerMP{
		private final Minecraft mc;
		private final NetHandlerPlayClient netHandler;
		
		public PlayerControllerMPOverride(Minecraft mc, NetHandlerPlayClient netHandler){
			super(mc, netHandler);
			this.mc = mc;
			this.netHandler = netHandler;
		}
		
		@Override
		public EntityPlayerSP func_192830_a(World world, StatisticsManager statFile, RecipeBook recipeBook){
			return new PlayerOverride(mc, world, netHandler, statFile, recipeBook);
		}
	}
}
