package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
abstract class GuiButtonCustomInput extends Button{
	public final int x, y;
	private final String titleKey;
	
	public GuiButtonCustomInput(int x, int y, String titleKey){
		super(x, y, 70, 20, new StringTextComponent(""), null);
		this.x = x;
		this.y = y;
		this.titleKey = titleKey;
	}
	
	@Override
	public abstract void func_230930_b_(); // RENAME onPress
	
	public boolean isMouseOver(int mouseX, int mouseY){
		return func_230992_c_(mouseX, mouseY);
	}
	
	public void setTitleKey(String translationKey){
		func_238482_a_(new TranslationTextComponent(translationKey)); // RENAME setMessage
	}
	
	public ITextComponent getTitle(){
		return new TranslationTextComponent(titleKey);
	}
	
	public ITextComponent[] getInfo(){
		return Arrays.stream(I18n.format(titleKey + ".info").split("#")).map(StringTextComponent::new).toArray(ITextComponent[]::new);
	}
}
