package chylex.bettersprinting.client.input;

public enum SprintKeyMode{
	TAP(SprintState.TAPPING_SPRINT_KEY, "bs.sprint.mode.tap"),
	HOLD(SprintState.HOLDING_SPRINT_KEY, "bs.sprint.mode.hold");
	
	public final SprintState sprintState;
	public final String translationKey;
	
	SprintKeyMode(SprintState sprintState, String translationKey){
		this.sprintState = sprintState;
		this.translationKey = translationKey;
	}
	
	public SprintKeyMode next(){
		SprintKeyMode[] values = values();
		return values[(ordinal() + 1) % values.length];
	}
}
