package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import chylex.bettersprinting.client.ClientModManager;

public class CustomMovementInput{
	public boolean sprint, sprintToggle, sneakToggle, hasToggledSprint, hasToggledSneak;
	
	public void update(Minecraft mc, MovementInputFromOptions options){
		options.moveStrafe = 0F;
		options.moveForward = 0F;
		
		GameSettings settings = mc.gameSettings;

		if (settings.keyBindForward.isKeyDown())++options.moveForward;
		if (settings.keyBindBack.isKeyDown())--options.moveForward;
		if (settings.keyBindLeft.isKeyDown())++options.moveStrafe;
		if (settings.keyBindRight.isKeyDown())--options.moveStrafe;
		
		options.jump = settings.keyBindJump.isKeyDown();
		options.sneak = settings.keyBindSneak.isKeyDown();
		
		sprint = ClientModManager.keyBindSprint.isKeyDown();
		
		if (!sprint){
			if (!ClientModManager.disableModFunctionality && ClientModManager.keyBindSprintToggle.isKeyDown()){
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
			if (!ClientModManager.disableModFunctionality && ClientModManager.keyBindSneakToggle.isKeyDown()){
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
	}
}
