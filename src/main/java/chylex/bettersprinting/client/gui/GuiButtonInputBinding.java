package chylex.bettersprinting.client.gui;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonInputBinding extends GuiButtonInputOption{
	private final Consumer<GuiButtonInputBinding> onClicked;
	
	public GuiButtonInputBinding(int id, int x, int y, KeyBinding binding, Consumer<GuiButtonInputBinding> onClick){
		super(id, x, y, binding == Minecraft.getMinecraft().gameSettings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
		this.displayString = GuiSprint.getKeyName(binding);
		this.onClicked = onClick;
	}
	
	@Override
	public void func_146113_a(SoundHandler soundHandler){
		super.func_146113_a(soundHandler);
		onClicked.accept(this);
	}
}
