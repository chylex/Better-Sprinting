package net.minecraft.src;

public class MovementInputFromOptions extends MovementInput
{
    private GameSettings gameSettings;
    public boolean sprint,sprintToggle,sneakToggle; // CHANGED LINE

    public MovementInputFromOptions(GameSettings par1GameSettings)
    {
        gameSettings = par1GameSettings;
        // CHANGE
        sprint = false;
        sprintToggle = false;
        sneakToggle = false;
        // END
    }

    public void func_52013_a()
    {
        moveStrafe = 0.0F;
        moveForward = 0.0F;

        if (gameSettings.keyBindForward.pressed)
        {
            moveForward++;
        }

        if (gameSettings.keyBindBack.pressed)
        {
            moveForward--;
        }

        if (gameSettings.keyBindLeft.pressed)
        {
            moveStrafe++;
        }

        if (gameSettings.keyBindRight.pressed)
        {
            moveStrafe--;
        }
        // CHANGE
        sprint = GuiControls.keyBindSprint.pressed;
        if (!sprint){
        	if (GuiControls.keyBindSprintToggle.pressed){
        		sprintToggle=!sprintToggle;
        		GuiControls.keyBindSprintToggle.pressed=false;
        	}
        	sprint=sprintToggle;
        }
        else sprintToggle=false;
        jump = gameSettings.keyBindJump.pressed;
        sneak = gameSettings.keyBindSneak.pressed;
        if (!sneak){
        	if (GuiControls.keyBindSneakToggle.pressed){
        		sneakToggle=!sneakToggle;
        		GuiControls.keyBindSneakToggle.pressed=false;
        	}
        	sneak=sneakToggle;
        }
        // END

        if (sneak)
        {
            moveStrafe *= 0.29999999999999999D;
            moveForward *= 0.29999999999999999D;
        }
    }
}
