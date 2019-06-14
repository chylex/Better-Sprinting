package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class GuiButtonInputBinding extends GuiButtonCustomInput{
	private final KeyBinding binding;
	private final Consumer<KeyBinding> onClicked;
	
	public GuiButtonInputBinding(int id, int x, int y, KeyBinding binding, Consumer<KeyBinding> onClick){
		super(id, x, y, binding.getLocalizedName(), binding == Minecraft.getInstance().gameSettings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
		this.binding = binding;
		this.onClicked = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(binding);
	}
}
