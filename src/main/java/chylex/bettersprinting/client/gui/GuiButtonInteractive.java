package chylex.bettersprinting.client.gui;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiButtonInteractive extends GuiButtonExt{
	private final Consumer<GuiButtonInteractive> onClick;
	
	public GuiButtonInteractive(int id, int x, int y, int width, int height, String displayText, Consumer<GuiButtonInteractive> onClick){
		super(id, x, y, width, height, displayText);
		this.onClick = onClick;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandler){
		super.playPressSound(soundHandler);
		onClick.accept(this);
	}
}
