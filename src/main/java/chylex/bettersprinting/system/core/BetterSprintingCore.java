package chylex.bettersprinting.system.core;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12")
public final class BetterSprintingCore implements IFMLLoadingPlugin{
	static boolean isObfuscated;
	static boolean allowTransform;
	
	@Override
	public String[] getASMTransformerClass(){
		return new String[]{ TransformerEntityPlayerSP.class.getName() };
	}

	@Override
	public String getModContainerClass(){
		return null;
	}

	@Override
	public String getSetupClass(){
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data){
		isObfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
		allowTransform = !((List)data.get("coremodList")).stream().anyMatch(o -> o.toString().startsWith("PlayerAPIPlugin"));
	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}
}
