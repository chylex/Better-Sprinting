package chylex.bettersprinting;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.ModConfig;
import java.util.function.Function;

public class BetterSprintingConfig{
	private final ModConfig config;
	
	BetterSprintingConfig(ModConfig config){ // TODO migrate Paths.get("config").resolve(modId + ".cfg").toFile()
		this.config = config;
	}
	
	public void save(){
		config.save();
	}
	
	public <T> void set(ConfigValue<T> property, T value){
		config.getConfigData().set(property.getPath(), value);
	}
	
	public <T> void update(ConfigValue<T> property, Function<T, T> func){
		set(property, func.apply(property.get()));
	}
}
