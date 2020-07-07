package chylex.bettersprinting;
import chylex.bettersprinting.client.ClientSetup;
import chylex.bettersprinting.server.ServerSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("bettersprinting")
public final class BetterSprintingMod{
	public BetterSprintingMod(){
		DistExecutor.safeRunWhenOn(Dist.CLIENT,           () -> ClientSetup::setup);
		DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerSetup::setup);
	}
}
