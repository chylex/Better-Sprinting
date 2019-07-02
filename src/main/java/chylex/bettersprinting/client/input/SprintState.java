package chylex.bettersprinting.client.input;

public enum SprintState{
	INACTIVE,
	DOUBLE_TAPPED_FORWARD,
	HOLDING_SPRINT_KEY,
	TAPPING_SPRINT_KEY,
	TAPPED_SPRINT_KEY,
	TOGGLED;
	
	public boolean active(){
		return this != INACTIVE;
	}
}
