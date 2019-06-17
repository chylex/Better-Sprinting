package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.MovementInput;
import chylex.bettersprinting.client.ClientModManager;

final class MovementController{
	public boolean sprint;
	
	private boolean sprintToggle, sneakToggle;
	private boolean hasToggledSprint, hasToggledSneak;
	private boolean shouldRestoreSneakToggle;
	
	private final Minecraft mc;
	private final MovementInput movementInput;
	
	public MovementController(MovementInput movementInput){
		this.mc = Minecraft.getMinecraft();
		this.movementInput = movementInput;
	}
	
	// UPDATE | Ensure replicated sneak modifications still match MovementInputFromOptions.updatePlayerMoveState | 1.8.9
	public void update(){
		sprint = ClientModManager.keyBindSprintHold.isKeyDown();
		
		if (!sprint){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSprintToggle.isKeyDown()){
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
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSneakToggle.isKeyDown()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else{
				hasToggledSneak = false;
			}
		}
		
		if (ClientModManager.isModDisabled()){
			sneakToggle = sprintToggle = false;
		}
		else{
			if (movementInput.sneak && sneakToggle && mc.currentScreen != null && !(mc.currentScreen instanceof GuiGameOver)){
				shouldRestoreSneakToggle = true;
				sneakToggle = false;
			}
			
			if (shouldRestoreSneakToggle && mc.currentScreen == null){
				sneakToggle = true;
				shouldRestoreSneakToggle = false;
			}
		}
		
		movementInput.updatePlayerMoveState();
		
		if (!movementInput.sneak && sneakToggle){
			movementInput.sneak = true;
			movementInput.moveStrafe *= 0.3F;
			movementInput.moveForward *= 0.3F;
		}
	}
	
	public boolean isMovingAnywhere(){
		return Math.abs(movementInput.moveForward) >= 1E-5F || Math.abs(movementInput.moveStrafe) >= 1E-5F;
	}
}
