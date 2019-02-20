package chylex.bettersprinting.client.gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import java.util.function.Consumer;

public class GuiButtonInputBinding extends GuiButtonExt{
	private final KeyBinding binding;
	private final Consumer<KeyBinding> onClicked;
	
	public GuiButtonInputBinding(int id, int x, int y, int width, int height, KeyBinding binding, Consumer<KeyBinding> onClick){
		super(id, x, y, width, height, binding.func_197978_k());
		this.binding = binding;
		this.onClicked = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(binding);
	}
}
