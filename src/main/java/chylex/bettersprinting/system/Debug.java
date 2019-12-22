package chylex.bettersprinting.system;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import org.lwjgl.glfw.GLFW;

public final class Debug{
	private static final boolean isDeobfEnvironment = System.getProperty("bettersprinting.debug") != null;
	
	@OnlyIn(Dist.CLIENT)
	public static void updateDebugWindowTitle(){
		if (isDeobfEnvironment){
			String mcVersion = SharedConstants.getVersion().getName();
			String modVersion = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
			
			String title = "Minecraft " + mcVersion + " - Better Sprinting " + modVersion;
			
			Minecraft mc = Minecraft.getInstance();
			mc.execute(() -> GLFW.glfwSetWindowTitle(mc.func_228018_at_().getHandle(), title));
		}
	}
}
