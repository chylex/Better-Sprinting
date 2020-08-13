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
	private final String titleKey;
	
	public GuiButtonCustomInput(final int x, final int y, final String titleKey){
		super(x, y, 70, 20, new StringTextComponent(""), null);
		this.titleKey = titleKey;
	}
	
	@Override
	public abstract void onPress();
	
	public void setTitleKey(final String translationKey){
		setMessage(new TranslationTextComponent(translationKey));
	}
	
	public ITextComponent getTitle(){
		return new TranslationTextComponent(titleKey);
	}
	
	public ITextComponent[] getInfo(){
		return Arrays.stream(I18n.format(titleKey + ".info").split("#")).map(StringTextComponent::new).toArray(ITextComponent[]::new);
	}
}
