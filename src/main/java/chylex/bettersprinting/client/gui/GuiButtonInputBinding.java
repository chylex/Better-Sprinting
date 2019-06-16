package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiButtonInputBinding extends GuiButtonInputOption{
	private final Consumer<GuiButtonInputBinding> onClicked;
	
	public GuiButtonInputBinding(int id, int x, int y, KeyBinding binding, Consumer<GuiButtonInputBinding> onClick){
		super(id, x, y, binding == Minecraft.getMinecraft().gameSettings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
		this.displayString = binding.getDisplayName();
		this.onClicked = onClick;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandler){
		super.playPressSound(soundHandler);
		onClicked.accept(this);
	}
}
