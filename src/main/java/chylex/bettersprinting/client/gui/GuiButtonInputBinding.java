package chylex.bettersprinting.client.gui;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class GuiButtonInputBinding extends GuiButtonCustomInput<GuiButtonInputBinding>{
	private static final GameSettings settings = Minecraft.getInstance().gameSettings;
	
	public final KeyBinding binding;
	private boolean isSelected;
	
	public GuiButtonInputBinding(int x, int y, KeyBinding binding, Consumer<GuiButtonInputBinding> onClick){
		super(x, y, "", binding == settings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription(), onClick);
		this.binding = binding;
		updateKeyBindingText();
	}
	
	@Override
	protected GuiButtonInputBinding getContext(){
		return this;
	}
	
	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
		updateKeyBindingText();
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public void setBinding(InputMappings.Input input){
		binding.bind(input);
		isSelected = false;
	}
	
	public void setBinding(KeyModifier modifier, InputMappings.Input input){
		binding.setKeyModifierAndCode(modifier, input);
		isSelected &= KeyModifier.isKeyCodeModifier(input) && modifier != KeyModifier.NONE;
	}
	
	public void updateKeyBindingText(){
		boolean hasConflict = false;
		boolean hasOnlyModifierConflict = true;
		
		if (!binding.isInvalid()){
			for(KeyBinding other:settings.keyBindings){
				if (binding != other && binding.conflicts(other)){
					hasConflict = true;
					hasOnlyModifierConflict &= binding.hasKeyCodeModifierConflict(other);
				}
			}
		}
		
		if (isSelected){
			setMessage(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + binding.getLocalizedName() + TextFormatting.WHITE + " <");
		}
		else if (hasConflict){
			setMessage((hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + binding.getLocalizedName());
		}
		else{
			setMessage(binding.getLocalizedName());
		}
	}
}
