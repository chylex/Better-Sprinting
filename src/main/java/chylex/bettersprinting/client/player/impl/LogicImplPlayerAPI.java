package chylex.bettersprinting.client.player.impl;
import api.player.client.ClientPlayerAPI;

public final class LogicImplPlayerAPI{
	public static void register(){
		ClientPlayerAPI.register("BetterSprinting",PlayerBase.class);
	}
	
	private LogicImplPlayerAPI(){}
}
