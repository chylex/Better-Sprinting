package chylex.bettersprinting.client.input;

public enum SprintState{
	INACTIVE,
	DOUBLE_TAPPED_FORWARD,
	HOLDING_SPRINT_KEY,
	TAPPING_SPRINT_KEY,
	TAPPED_SPRINT_KEY,
	TOGGLED,
	TOGGLED_WHILE_HOLDING_SPRINT_KEY;
	
	public boolean active(){
		return this != INACTIVE;
	}
	
	public boolean toggled(){
		return this == TOGGLED || this == TOGGLED_WHILE_HOLDING_SPRINT_KEY;
	}
}
