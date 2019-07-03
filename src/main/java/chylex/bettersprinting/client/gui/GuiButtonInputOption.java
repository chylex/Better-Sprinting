package chylex.bettersprinting.client.gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public final class GuiButtonInputOption extends GuiButtonCustomInput<Integer>{
	public GuiButtonInputOption(int id, int x, int y, String titleKey, Consumer<Integer> onClick){
		super(id, x, y, "", titleKey, onClick);
	}
	
	@Override
	protected Integer getContext(){
		return id;
	}
}
