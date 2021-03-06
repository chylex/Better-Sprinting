package chylex.bettersprinting.client.input;
import chylex.bettersprinting.BetterSprintingConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public final class KeyBindingInfo{
	private final IntValue keyCode;
	private final EnumValue<KeyModifier> keyModifier;
	private final EnumValue<InputMappings.Type> keyType;
	
	public KeyBindingInfo(final IntValue keyCode, final EnumValue<KeyModifier> keyModifier, final EnumValue<InputMappings.Type> keyType){
		this.keyCode = keyCode;
		this.keyModifier = keyModifier;
		this.keyType = keyType;
	}
	
	public void set(final KeyModifier modifier, final InputMappings.Input input){
		BetterSprintingConfig.set(keyCode, input.getKeyCode());
		BetterSprintingConfig.set(keyModifier, modifier);
		BetterSprintingConfig.set(keyType, input.getType());
	}
	
	public void readFrom(final KeyBinding binding){
		set(binding.getKeyModifier(), binding.getKey());
	}
	
	public void writeInto(final KeyBinding binding){
		binding.setKeyModifierAndCode(keyModifier.get(), keyType.get().getOrMakeInput(keyCode.get()));
	}
}
