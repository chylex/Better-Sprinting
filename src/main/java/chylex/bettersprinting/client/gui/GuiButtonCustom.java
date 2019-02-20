package chylex.bettersprinting.client.gui;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import java.util.function.Consumer;

public class GuiButtonCustom extends GuiButtonExt{
	private final Consumer<GuiButtonCustom> onClicked;
	
	public GuiButtonCustom(int id, int x, int y, String title, Consumer<GuiButtonCustom> onClick){
		super(id, x, y, title);
		this.onClicked = onClick;
	}
	
	public GuiButtonCustom(int id, int x, int y, int width, int height, String title, Consumer<GuiButtonCustom> onClick){
		super(id, x, y, width, height, title);
		this.onClicked = onClick;
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_){
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(this);
	}
}
