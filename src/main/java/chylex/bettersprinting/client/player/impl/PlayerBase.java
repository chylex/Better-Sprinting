package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import chylex.bettersprinting.client.player.PlayerLogicHandler;

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
		LivingUpdate.callPreSuper(player,mc,logic);
		playerAPI.superOnLivingUpdate();
		LivingUpdate.callPostSuper(player,mc,logic);
	}
}
