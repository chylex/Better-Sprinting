package chylex.bettersprinting.client.player.impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.player.LivingUpdate;

@SideOnly(Side.CLIENT)
public final class LogicImplPlayerAPI{
	public static void register(){
		ClientPlayerAPI.register(BetterSprintingMod.modId, PlayerBase.class);
	}
	
	@SideOnly(Side.CLIENT)
	private static class PlayerBase extends ClientPlayerBase{ // TODO test when PAPI comes out
		public PlayerBase(ClientPlayerAPI api){
			super(api);
		}
		
		@Override
		public void onLivingUpdate(){
			LivingUpdate.callPreSuper(player);
			playerAPI.superOnLivingUpdate();
			LivingUpdate.callPostSuper(player);
		}
	}
}
