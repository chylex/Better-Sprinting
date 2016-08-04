package chylex.bettersprinting.system;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import chylex.bettersprinting.BetterSprintingMod;

public final class Log{
	static final Logger logger = LogManager.getLogger("BetterSprinting");
	
	public static boolean isDeobfEnvironment;
	
	public static void load(){
		isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
		
		if (isDeobfEnvironment && FMLCommonHandler.instance().getSide() == Side.SERVER){
			try(FileOutputStream fos = new FileOutputStream(new File("eula.txt"))){
				Properties properties = new Properties();
				properties.setProperty("eula","true");
				properties.store(fos,"Screw your EULA, I don't want that stuff in my workspace.");
			}catch(IOException e){}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initializeDebug(){
		if (isDeobfEnvironment){
			Display.setTitle(new StringBuilder().append(Display.getTitle()).append(" - BetterSprinting - ").append(isDeobfEnvironment ? "dev" : "debug").append(' ').append(BetterSprintingMod.modVersion).toString());
		}
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void debug(String message, Object...data){
		if (isDeobfEnvironment)logger.info(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void info(String message, Object...data){
		logger.info(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void warn(String message, Object...data){
		logger.warn(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void error(String message, Object...data){
		logger.error(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void throwable(Throwable throwable, String message, Object...data){
		logger.catching(Level.ERROR,throwable);
		logger.error(getMessage(message,data));
	}
	
	private static String getMessage(String message, Object...data){
		for(int a = data.length-1; a >= 0; a--)message = message.replace("$"+a,data[a] == null ? "null" : String.valueOf(data[a]));
		return message;
	}
}
