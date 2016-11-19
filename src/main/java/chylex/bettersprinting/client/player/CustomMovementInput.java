package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import chylex.bettersprinting.client.ClientModManager;

public class CustomMovementInput{
	public boolean held = false;
	public int stoptime = 0;
	public boolean sprint, sprintToggle, sneakToggle, hasToggledSprint, hasToggledSneak;
	
	// UPDATE | MovementInputFromOptions.updatePlayerMoveState | 1.10.2
	public void update(Minecraft mc, MovementInputFromOptions options){
		options.moveStrafe = 0F;
		options.moveForward = 0F;
		
		GameSettings settings = mc.gameSettings;

		if (settings.keyBindForward.isKeyDown()){
			++options.moveForward;
			options.forwardKeyDown = true;
		}
		else options.forwardKeyDown = false;
		
		if (settings.keyBindBack.isKeyDown()){
			--options.moveForward;
			options.backKeyDown = true;
		}
		else options.backKeyDown = false;
		
		if (settings.keyBindLeft.isKeyDown()){
			++options.moveStrafe;
			options.leftKeyDown = true;
		}
		else options.leftKeyDown = false;
		
		if (settings.keyBindRight.isKeyDown()){
			--options.moveStrafe;
			options.rightKeyDown = true;
		}
		else options.rightKeyDown = false;
		
		// custom handling
		sprint = ClientModManager.keyBindSprintHold.isKeyDown();
		
		if (!sprint){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSprintToggle.isKeyDown()){
				if (!hasToggledSprint){
					sprintToggle = !sprintToggle;
					hasToggledSprint = true;
				}
			}
			else hasToggledSprint = false;

			sprint = sprintToggle;
		}
		else sprintToggle = false;
		
		options.jump = settings.keyBindJump.isKeyDown();
		options.sneak = settings.keyBindSneak.isKeyDown();
		
		if (!options.sneak){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSneakToggle.isKeyDown()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else hasToggledSneak = false;
			
			options.sneak = sneakToggle;
		}
		
		if (options.sneak){
			options.moveStrafe = options.moveStrafe*0.3F;
			options.moveForward = options.moveForward*0.3F;
		}
		
		if (ClientModManager.isModDisabled()){
			sneakToggle = sprintToggle = false;
		}
	}
}
