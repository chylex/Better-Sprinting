package chylex.bettersprinting.system;
import chylex.bettersprinting.BetterSprintingMod;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public final class Log{
	private static final boolean isDeobfEnvironment = System.getProperty("bettersprinting.debug") != null;
	private static final Logger logger = LogManager.getLogger("BetterSprinting");
	
	public static void load(){
		if (isDeobfEnvironment){
			DistExecutor.runWhenOn(Dist.CLIENT, () -> Log::loadDeobfClient);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private static void loadDeobfClient(){
		String title = "Minecraft " + RealmsSharedConstants.VERSION_STRING + " - BetterSprinting " + BetterSprintingMod.modVersion;
		
		Minecraft mc = Minecraft.getInstance();
		mc.addScheduledTask(() -> GLFW.glfwSetWindowTitle(mc.mainWindow.getHandle(), title));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void debug(String message, Object...data){
		if (isDeobfEnvironment){
			logger.info(getMessage(message, data));
		}
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
		for(int a = data.length - 1; a >= 0; a--){
			message = message.replace("$" + a, data[a] == null ? "null" : String.valueOf(data[a]));
		}
		
		return message;
	}
}
