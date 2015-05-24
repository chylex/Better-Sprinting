package chylex.bettersprinting.client.player.impl;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import api.player.client.ClientPlayerAPI;

@SideOnly(Side.CLIENT)
public final class LogicImplPlayerAPI{
	public static void register(){
		ClientPlayerAPI.register("BetterSprinting",PlayerBase.class);
	}
	
	private LogicImplPlayerAPI(){}
}
