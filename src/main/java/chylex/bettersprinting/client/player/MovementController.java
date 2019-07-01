package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.MovementInput;

final class MovementController{
	public boolean sprint;
	
	private boolean sprintToggle, sneakToggle;
	private boolean hasToggledSprint, hasToggledSneak;
	private boolean shouldRestoreSneakToggle;
	
	private final Minecraft mc;
	private final MovementInput movementInput;
	
	public MovementController(MovementInput movementInput){
		this.mc = Minecraft.getInstance();
		this.movementInput = movementInput;
	}
	
	// UPDATE | Ensure first parameter of MovementInputFromOptions.func_217607_a still behaves like forced sneak | 1.14.3
	public void update(boolean slowMovement, boolean isSpectator){
		sprint = ClientModManager.keyBindSprintHold.isKeyDown();
		
		if (!sprint){
			if (ClientModManager.keyBindSprintToggle.isKeyDown()){
				if (!hasToggledSprint){
					sprintToggle = !sprintToggle;
					hasToggledSprint = true;
				}
			}
			else{
				hasToggledSprint = false;
			}
			
			sprint = sprintToggle;
		}
		else{
			sprintToggle = false;
		}
		
		if (!mc.gameSettings.keyBindSneak.isKeyDown()){
			if (ClientModManager.keyBindSneakToggle.isKeyDown()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else{
				hasToggledSneak = false;
			}
		}
		
		if (movementInput.sneak && sneakToggle && mc.currentScreen != null && !(mc.currentScreen instanceof DeathScreen)){
			shouldRestoreSneakToggle = true;
			sneakToggle = false;
		}
		
		if (shouldRestoreSneakToggle && mc.currentScreen == null){
			sneakToggle = true;
			shouldRestoreSneakToggle = false;
		}
		
		movementInput.func_217607_a(slowMovement || sneakToggle, isSpectator);
		movementInput.sneak |= sneakToggle;
	}
	
	public boolean isMovingAnywhere(){
		return Math.abs(movementInput.moveForward) >= 1E-5F || Math.abs(movementInput.moveStrafe) >= 1E-5F;
	}
}
