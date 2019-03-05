package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class GuiButtonCustom extends GuiButtonExt{
	private final Consumer<GuiButtonCustom> onClicked;
	
	public GuiButtonCustom(int id, int x, int y, int width, int height, String buttonText, Consumer<GuiButtonCustom> onClick){
		super(id, x, y, width, height, buttonText);
		this.onClicked = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(this);
	}
}
