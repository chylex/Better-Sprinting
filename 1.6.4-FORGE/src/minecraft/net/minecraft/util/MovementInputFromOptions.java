package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import chylex.bettersprinting.mod.GuiSprint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
        sprint = GuiSprint.keyBindSprint.pressed;
        if (!sprint){
        	if (GuiSprint.keyBindSprintToggle.pressed){
        		sprintToggle=!sprintToggle;
        		GuiSprint.keyBindSprintToggle.pressed=false;
        	}
        	sprint=sprintToggle;
        }
        else sprintToggle=false;
        jump = gameSettings.keyBindJump.pressed;
        sneak = gameSettings.keyBindSneak.pressed;
        if (!sneak){
        	if (GuiSprint.keyBindSneakToggle.pressed){
        		sneakToggle=!sneakToggle;
        		GuiSprint.keyBindSneakToggle.pressed=false;
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
