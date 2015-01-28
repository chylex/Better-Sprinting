package chylex.bettersprinting.system;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import chylex.bettersprinting.BetterSprintingMod;

public final class Log{
	static final Logger logger = LogManager.getLogger("BetterSprinting");
	
	public static final boolean isDeobfEnvironment;
	public static boolean forceDebugEnabled;
	private static byte obfEnvironmentWarning = 0;
	
	static{
		isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
		
		if (isDeobfEnvironment && MinecraftServer.getServer().getClass().getSimpleName().equals("DedicatedServer")){
			FileOutputStream fos = null;
			
			try{
				Properties properties = new Properties();
				properties.setProperty("eula","true");
				properties.store(fos = new FileOutputStream(new File("eula.txt")),"Screw your EULA, I don't want that stuff in my workspace.");
			}catch(Exception e){
			}finally{
				try{
					if (fos != null)fos.close();
				}catch(IOException e){}
			}
		}
	}
	
	public static void initializeDebug(){
		if (forceDebugEnabled || isDeobfEnvironment){
			Display.setTitle(new StringBuilder().append(Display.getTitle()).append(" - BetterSprinting - ").append(isDeobfEnvironment ? "dev" : "debug").append(' ').append(BetterSprintingMod.modVersion).toString());
		}
	}
	
	public static boolean isDebugEnabled(){
		return forceDebugEnabled || isDeobfEnvironment;
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void debug(String message, Object...data){
		if (forceDebugEnabled || isDeobfEnvironment)logger.info(getMessage(message,data));
		
		if (forceDebugEnabled && !isDeobfEnvironment && ++obfEnvironmentWarning >= 30){
			logger.warn(getMessage("Detected obfuscated environment, don't forget to disable logging debug info after you are done debugging!"));
			obfEnvironmentWarning = 0;
		}
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
