package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class GuiScreenMigration extends Screen{
	protected final List<Widget> buttons;
	
	protected int width, height;
	protected FontRenderer font;
	
	protected GuiScreenMigration(ITextComponent title){
		super(title);
		buttons = field_230710_m_;
	}
	
	protected <T extends Widget> T addButton(T button){
		return super.func_230480_a_(button);
	}
	
	@Override
	public void func_231158_b_(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_){
		super.func_231158_b_(p_231158_1_, p_231158_2_, p_231158_3_);
		width = field_230708_k_;
		height = field_230709_l_;
		font = field_230712_o_;
		init();
	}
	
	protected abstract void init();
	
	@Override
	protected final void func_231160_c_(){}
}
