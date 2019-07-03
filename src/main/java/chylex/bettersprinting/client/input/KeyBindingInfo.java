package chylex.bettersprinting.client.input;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class KeyBindingInfo{
	private final IntConsumer keyCodeSet;
	private final IntSupplier keyCodeGet;
	
	private final Consumer<KeyModifier> keyModifierSet;
	private final Supplier<KeyModifier> keyModifierGet;
	
	public KeyBindingInfo(IntConsumer keyCodeSet, IntSupplier keyCodeGet, Consumer<KeyModifier> keyModifierSet, Supplier<KeyModifier> keyModifierGet){
		this.keyCodeSet = keyCodeSet;
		this.keyCodeGet = keyCodeGet;
		this.keyModifierSet = keyModifierSet;
		this.keyModifierGet = keyModifierGet;
	}
	
	public void set(KeyModifier modifier, int code){
		keyCodeSet.accept(code);
		keyModifierSet.accept(modifier);
	}
	
	public void readFrom(KeyBinding binding){
		set(binding.getKeyModifier(), keyCodeGet.getAsInt());
	}
	
	public void writeInto(KeyBinding binding){
		binding.setKeyModifierAndCode(keyModifierGet.get(), keyCodeGet.getAsInt());
	}
}
