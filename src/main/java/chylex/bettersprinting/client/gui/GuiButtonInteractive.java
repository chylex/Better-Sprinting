package chylex.bettersprinting.client.gui;
import java.util.function.Consumer;
import net.minecraft.client.audio.SoundHandler;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonInteractive extends GuiButtonExt{
	private final Consumer<GuiButtonInteractive> onClick;
	
	public GuiButtonInteractive(int id, int x, int y, int width, int height, String displayText, Consumer<GuiButtonInteractive> onClick){
		super(id, x, y, width, height, displayText);
		this.onClick = onClick;
	}
	
	@Override
	public void func_146113_a(SoundHandler soundHandler){
		super.func_146113_a(soundHandler);
		onClick.accept(this);
	}
}
