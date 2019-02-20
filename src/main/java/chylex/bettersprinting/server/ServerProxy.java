package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingProxy;
import chylex.bettersprinting.system.PacketPipeline;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public final class ServerProxy extends BetterSprintingProxy{
	static String mcVersion = "";
	
	@Override
	public String getMinecraftVersion(){
		return mcVersion;
	}
	
	@Override
	public void onConstructed(ModLoadingContext ctx){
		ServerSettings.register(ctx);
		ServerEventHandler.register();
		PacketPipeline.initialize(new ServerNetwork());
	}

	@Override
	public void onLoaded(FMLLoadCompleteEvent e){}
}
