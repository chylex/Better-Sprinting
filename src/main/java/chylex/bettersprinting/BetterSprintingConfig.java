package chylex.bettersprinting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

public final class BetterSprintingConfig{
	private static BetterSprintingConfig instance;
	
	public static void initialize(final ModConfig.Type type, final ForgeConfigSpec spec, final String suffix){
		instance = new BetterSprintingConfig();
		
		final String fileName = "bettersprinting-" + suffix + ".toml";
		ModLoadingContext.get().registerConfig(type, spec, fileName);
		
		if (Files.notExists(Paths.get("config", fileName))){
			instance.isNew = true; // since keybinds are not migrated and first time setup only modifies keybinds, this is fine
		}
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::onConfigLoaded);
	}
	
	private ModConfig config;
	private boolean isNew = false;
	
	private BetterSprintingConfig(){}
	
	private void onConfigLoaded(final ModConfig.Loading e){
		config = e.getConfig();
	}
	
	public static boolean isNew(){
		return instance.isNew;
	}
	
	public static void save(){
		instance.config.save();
	}
	
	public static <T> void set(final ConfigValue<T> property, final T value){
		instance.config.getConfigData().set(property.getPath(), value);
	}
	
	public static <T> void update(final ConfigValue<T> property, final UnaryOperator<T> func){
		set(property, func.apply(property.get()));
	}
}
