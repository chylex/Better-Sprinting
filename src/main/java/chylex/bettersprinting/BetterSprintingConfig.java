package chylex.bettersprinting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public final class BetterSprintingConfig{
	private final ModConfig config;
	
	BetterSprintingConfig(ModConfig config){
		this.config = config;
	}
	
	public boolean isNew(){
		return isNew;
	}
	
	public void save(){
		config.save();
	}
	
	public <T> void set(ConfigValue<T> property, T value){
		config.getConfigData().set(property.getPath(), value);
	}
	
	public <T> void update(ConfigValue<T> property, UnaryOperator<T> func){
		set(property, func.apply(property.get()));
	}
	
	// Migration
	
	private static Path migrationFile = null;
	private static boolean isNew = false;
	
	// TODO remove migration eventually
	
	void migrate(){
		if (migrationFile != null && Files.exists(migrationFile)){
			BetterSprintingMod.proxy.migrateConfig(readOldConfig(migrationFile));
			save();
		}
	}
	
	public static void register(ModLoadingContext context, ModConfig.Type type, ForgeConfigSpec spec, String suffix){
		String fileName = BetterSprintingMod.id + "-" + suffix + ".toml";
		context.registerConfig(type, spec, fileName);
		
		if (Files.notExists(Paths.get("config", fileName))){
			migrationFile = Paths.get("config", "bettersprinting.cfg").toAbsolutePath();
			isNew = true; // since keybinds are not migrated and first time setup only modifies keybinds, this is fine
		}
	}
	
	private static Triple<Character, String, String> readOldConfigEntry(String trimmedLine){
		String[] contents = trimmedLine.substring(2).split("=", 2);
		return new ImmutableTriple<>(trimmedLine.charAt(0), contents[0], contents[1]);
	}
	
	private static List<Triple<Character, String, String>> readOldConfig(Path oldFilePath){
		List<Triple<Character, String, String>> oldConfig = new ArrayList<>();
		
		try{
			Files.readAllLines(oldFilePath)
			     .stream()
			     .map(String::trim)
			     .filter(line -> line.length() > 4 && line.charAt(1) == ':')
			     .map(BetterSprintingConfig::readOldConfigEntry)
			     .forEach(oldConfig::add);
		}catch(IOException e){
			BetterSprintingMod.log.catching(e);
			BetterSprintingMod.log.error("Failed migrating old configuration.");
		}
		
		return oldConfig;
	}
}
