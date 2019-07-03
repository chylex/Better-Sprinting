package chylex.bettersprinting.client.gui;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public abstract class GuiButtonCustomInput<T> extends GuiButton{
	private final String titleKey;
	private final Consumer<T> onClick;
	
	public GuiButtonCustomInput(int id, int x, int y, String buttonText, String titleKey, Consumer<T> onClick){
		super(id, x, y, 70, 20, buttonText);
		this.titleKey = titleKey;
		this.onClick = onClick;
	}
	
	protected abstract T getContext();
	
	@Override
	public void playPressSound(SoundHandler soundHandler){
		super.playPressSound(soundHandler);
		onClick.accept(getContext());
	}
	
	public void setMessage(String message){
		displayString = message;
	}
	
	public void setTitleKey(String translationKey){
		setMessage(I18n.format(translationKey));
	}
	
	public String getTitle(){
		return I18n.format(titleKey);
	}
	
	public String[] getInfo(){
		return I18n.format(titleKey + ".info").split("#");
	}
}
