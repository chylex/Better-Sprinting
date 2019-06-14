package chylex.bettersprinting.system;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log{
	private static final Logger logger = LogManager.getLogger("BetterSprinting");
	private static boolean isDeobfEnvironment;
	
	public static void load(){
		isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void debug(String message, Object...data){
		if (isDeobfEnvironment)logger.info(getMessage(message, data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void info(String message, Object...data){
		logger.info(getMessage(message, data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void warn(String message, Object...data){
		logger.warn(getMessage(message, data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void error(String message, Object...data){
		logger.error(getMessage(message, data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void throwable(Throwable throwable, String message, Object...data){
		logger.catching(Level.ERROR, throwable);
		logger.error(getMessage(message, data));
	}
	
	private static String getMessage(String message, Object...data){
		for(int a = data.length - 1; a >= 0; a--)message = message.replace("$" + a, data[a] == null ? "null" : String.valueOf(data[a]));
		return message;
	}
}
