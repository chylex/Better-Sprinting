package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public final class GuiButtonInputBinding extends GuiButtonCustomInput<GuiButtonInputBinding>{
	private static final GameSettings settings = Minecraft.getMinecraft().gameSettings;
	
	public final KeyBinding binding;
	private boolean isSelected;
	
	public GuiButtonInputBinding(int id, int x, int y, KeyBinding binding, Consumer<GuiButtonInputBinding> onClick){
		super(id, x, y, "", binding == settings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription(), onClick);
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
	
	public void setBinding(int keyCode){
		binding.setKeyModifierAndCode(KeyModifier.NONE, keyCode);
		isSelected = false;
	}
	
	public void setBinding(KeyModifier modifier, int keyCode){
		binding.setKeyModifierAndCode(modifier, keyCode);
		isSelected &= KeyModifier.isKeyCodeModifier(keyCode) && modifier != KeyModifier.NONE;
	}
	
	public void updateKeyBindingText(){
		boolean hasConflict = false;
		boolean hasOnlyModifierConflict = true;
		
		if (binding.getKeyCode() != 0){
			for(KeyBinding other:settings.keyBindings){
				if (binding != other && binding.conflicts(other)){
					hasConflict = true;
					hasOnlyModifierConflict &= binding.hasKeyCodeModifierConflict(other);
				}
			}
		}
		
		if (isSelected){
			setMessage(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + binding.getDisplayName() + TextFormatting.WHITE + " <");
		}
		else if (hasConflict){
			setMessage((hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + binding.getDisplayName());
		}
		else{
			setMessage(binding.getDisplayName());
		}
	}
}
