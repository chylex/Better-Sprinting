package chylex.bettersprinting.client.gui;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import java.util.function.Consumer;

public class GuiButtonCustom extends GuiButtonExt{
	private final Consumer<GuiButtonCustom> onClicked;
	
	public GuiButtonCustom(int id, int x, int y, int width, int height, String title, Consumer<GuiButtonCustom> onClick){
		super(id, x, y, width, height, title);
		this.onClicked = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(this);
	}
}
