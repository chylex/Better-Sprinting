package chylex.bettersprinting.client.input;
import net.minecraft.util.text.TranslationTextComponent;

public enum SprintKeyMode{
	TAP(SprintState.TAPPING_SPRINT_KEY, "bs.sprint.mode.tap"),
	HOLD(SprintState.HOLDING_SPRINT_KEY, "bs.sprint.mode.hold");
	
	public final SprintState sprintState;
	public final TranslationTextComponent translationKey;
	
	SprintKeyMode(SprintState sprintState, String translationKey){
		this.sprintState = sprintState;
		this.translationKey = new TranslationTextComponent(translationKey);
	}
	
	public SprintKeyMode next(){
		SprintKeyMode[] values = values();
		return values[(ordinal() + 1) % values.length];
	}
}
