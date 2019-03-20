package net.minecraft.util;

import net.minecraft.client.gui.GuiSprint;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    public GameSettings gameSettings;
    // CHANGE + publicized gameSettings
    public boolean sprint = false, sprintToggle = false, sneakToggle = false,
    			   hasToggledSprint = false, hasToggledSneak = false;
    // END
    private static final String __OBFID = "CL_00000937";

    public MovementInputFromOptions(GameSettings par1GameSettings)
    {
        this.gameSettings = par1GameSettings;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.func_151470_d())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.func_151470_d())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.func_151470_d())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.func_151470_d())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.func_151470_d();
        this.sneak = this.gameSettings.keyBindSneak.func_151470_d();
        
        // CHANGE
        sprint = GuiSprint.keyBindSprint.func_151470_d();
        if (!sprint){
        	if (!GuiSprint.disableModFunctionality&&GuiSprint.keyBindSprintToggle.func_151470_d()){
        		if (!hasToggledSprint){
        			sprintToggle=!sprintToggle;
        			hasToggledSprint=true;
        		}
        	}
        	else hasToggledSprint=false;
        	
        	sprint=sprintToggle;
        }
        else sprintToggle=false;
        
        jump = gameSettings.keyBindJump.func_151470_d();
        sneak = gameSettings.keyBindSneak.func_151470_d();
        
        if (!sneak){
        	if (!GuiSprint.disableModFunctionality&&GuiSprint.keyBindSneakToggle.func_151470_d()){
        		if (!hasToggledSneak){
        			sneakToggle=!sneakToggle;
        			hasToggledSneak=true;
        		}
        	}
        	else hasToggledSneak=false;
        	
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
