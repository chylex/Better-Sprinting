package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import chylex.bettersprinting.client.player.PlayerLogicHandler;

@SideOnly(Side.CLIENT)
public class PlayerBase extends ClientPlayerBase{
	private final Minecraft mc;
	private final PlayerLogicHandler logic;
	
	public PlayerBase(ClientPlayerAPI api){
		super(api);
		mc = Minecraft.getMinecraft();
		logic = new PlayerLogicHandler();
	}
	
	@Override
	public void onLivingUpdate(){
		logic.setPlayer(player);
		LivingUpdate.callPreSuper(player, logic);
		playerAPI.superOnLivingUpdate();
		LivingUpdate.callPostSuper(player, logic);
	}
}
