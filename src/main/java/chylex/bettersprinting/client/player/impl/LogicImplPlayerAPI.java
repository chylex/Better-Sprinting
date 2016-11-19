package chylex.bettersprinting.client.player.impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import api.player.client.ClientPlayerAPI;
import chylex.bettersprinting.BetterSprintingMod;

@SideOnly(Side.CLIENT)
public final class LogicImplPlayerAPI{
	public static void register(){
		ClientPlayerAPI.register(BetterSprintingMod.modId, PlayerBase.class);
	}
	
	private LogicImplPlayerAPI(){}
}
