package chylex.bettersprinting.client.gui;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class GuiButtonInputBinding extends GuiButtonCustomInput{
	private static final GameSettings settings = Minecraft.getInstance().gameSettings;
	
	public final KeyBinding binding;
	private final Consumer<GuiButtonInputBinding> onClick;
	private boolean isSelected;
	
	public GuiButtonInputBinding(int x, int y, KeyBinding binding, Consumer<GuiButtonInputBinding> onClick){
		super(x, y, binding == settings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
		this.binding = binding;
		this.onClick = onClick;
		updateKeyBindingText();
	}
	
	@Override
	public void func_230930_b_(){ // RENAME onPress
		onClick.accept(this);
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
		
		// RENAME KeyBindingList
		if (isSelected){
			func_238482_a_((new StringTextComponent("> ")).func_230529_a_(binding.func_238171_j_().func_230532_e_().func_240699_a_(TextFormatting.YELLOW)).func_240702_b_(" <").func_240699_a_(TextFormatting.YELLOW));
		}
		else if (hasConflict){
			func_238482_a_(binding.func_238171_j_().func_230532_e_().func_240699_a_(hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED));
		}
		else{
			func_238482_a_(binding.func_238171_j_());
		}
	}
}
