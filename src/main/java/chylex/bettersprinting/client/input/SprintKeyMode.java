package chylex.bettersprinting.client.input;
import net.minecraft.util.text.TranslationTextComponent;

public enum SprintKeyMode{
	TAP(SprintState.TAPPING_SPRINT_KEY, "bs.sprint.mode.tap"),
	HOLD(SprintState.HOLDING_SPRINT_KEY, "bs.sprint.mode.hold");
	
	public final SprintState sprintState;
	public final TranslationTextComponent translationKey;
	
	SprintKeyMode(final SprintState sprintState, final String translationKey){
		this.sprintState = sprintState;
		this.translationKey = new TranslationTextComponent(translationKey);
	}
	
	public SprintKeyMode next(){
		final SprintKeyMode[] values = values();
		return values[(ordinal() + 1) % values.length];
	}
}
