package net.minecraft.src;

public class MovementInputFromOptions extends MovementInput
{
    private GameSettings gameSettings;
    public boolean sprint=false,sprintToggle=false,sneakToggle=false; // CHANGED LINE

    public MovementInputFromOptions(GameSettings par1GameSettings)
    {
        this.gameSettings = par1GameSettings;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.pressed)
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.pressed)
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.pressed)
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.pressed)
        {
            --this.moveStrafe;
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

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }
    }
}
