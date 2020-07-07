package chylex.bettersprinting.server;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class ServerSettings{
	public static final BooleanValue enableSurvivalFlyBoost;
	public static final BooleanValue enableAllDirs;
	public static final BooleanValue disableClientMod;
	
	private static final ForgeConfigSpec configSpec;
	
	public static ForgeConfigSpec getSpec(){
		return configSpec;
	}
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		builder.push("server");
		
		enableSurvivalFlyBoost = builder.define("enableSurvivalFlyBoost", false);
		enableAllDirs          = builder.define("enableAllDirs", false);
		disableClientMod       = builder.define("disableClientMod", false);
		
		builder.pop();
		
		configSpec = builder.build();
	}
}
