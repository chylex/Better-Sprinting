package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;

public class CustomMovementInput{
	public boolean sprint = false, sprintToggle = false, sneakToggle = false,
			   	   hasToggledSprint = false, hasToggledSneak = false;
	
	public void update(Minecraft mc, MovementInputFromOptions options){
		options.moveStrafe=0.0F;
		options.moveForward=0.0F;
		
		GameSettings settings=mc.gameSettings;

		if (settings.keyBindForward.getIsKeyPressed()){
			++options.moveForward;
		}

		if (settings.keyBindBack.getIsKeyPressed()){
			--options.moveForward;
		}

		if (settings.keyBindLeft.getIsKeyPressed()){
			++options.moveStrafe;
		}

		if (settings.keyBindRight.getIsKeyPressed()){
			--options.moveStrafe;
		}

		options.jump=settings.keyBindJump.getIsKeyPressed();
		options.sneak=settings.keyBindSneak.getIsKeyPressed();

		// CHANGE
		sprint=ClientModManager.keyBindSprint.getIsKeyPressed();
		if (!sprint){
			if (!ClientModManager.disableModFunctionality&&ClientModManager.keyBindSprintToggle.getIsKeyPressed()){
				if (!hasToggledSprint){
					sprintToggle=!sprintToggle;
					hasToggledSprint=true;
				}
			}
			else hasToggledSprint=false;

			sprint=sprintToggle;
		}
		else sprintToggle=false;

		options.jump=settings.keyBindJump.getIsKeyPressed();
		options.sneak=settings.keyBindSneak.getIsKeyPressed();

		if (!options.sneak){
			if (!ClientModManager.disableModFunctionality&&ClientModManager.keyBindSneakToggle.getIsKeyPressed()){
				if (!hasToggledSneak){
					sneakToggle=!sneakToggle;
					hasToggledSneak=true;
				}
			}
			else hasToggledSneak=false;

			options.sneak=sneakToggle;
		}
		// END

		if (options.sneak){
			options.moveStrafe=(float)(options.moveStrafe*0.3D);
			options.moveForward=(float)(options.moveForward*0.3D);
		}
	}
}
