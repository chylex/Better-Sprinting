package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class GuiButtonInputOption extends GuiButtonCustomInput<Integer>{
	public GuiButtonInputOption(int id, int x, int y, String titleKey, Consumer<Integer> onClick){
		super(id, x, y, "", titleKey, onClick);
	}
	
	@Override
	protected Integer getContext(){
		return id;
	}
}
