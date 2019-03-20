package net.minecraft.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import chylex.bettersprinting.mod.GuiSprint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreen extends Gui
{
    /** Reference to the Minecraft object. */
    public Minecraft mc;

    /** The width of the screen object. */
    public int width;

    /** The height of the screen object. */
    public int height;

    /** A list of all the buttons in this container. */
    public List buttonList = new ArrayList();
    public boolean allowUserInput;

    /** The FontRenderer used by GuiScreen */
    public FontRenderer fontRenderer;

    /** The button that was just pressed. */
    public GuiButton selectedButton;
    public int eventButton;
    public long lastMouseEvent;
    public int field_92018_d;
    
    // CHANGE + made everything public
    public static boolean held = false, hasChanged = false, fromBs = false;
    public static int stoptime = 0;
    
    public static boolean canRunInAllDirs(Minecraft mc){
    	return (mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||GuiSprint.svRunInAllDirs);
    }
    
    public static boolean canBoostFlying(Minecraft mc){
    	return (mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||mc.thePlayer.capabilities.isCreativeMode||GuiSprint.svFlyingBoost);
    }
    
    public int prevWidth=0, prevHeight=0;
    // END

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        for (int k = 0; k < this.buttonList.size(); ++k)
        {
            GuiButton guibutton = (GuiButton)this.buttonList.get(k);
            guibutton.drawButton(this.mc, par1, par2);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString()
    {
        try
        {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception exception)
        {
            ;
        }

        return "";
    }

    /**
     * store a string in the system clipboard
     */
    public static void setClipboardString(String par0Str)
    {
        try
        {
            StringSelection stringselection = new StringSelection(par0Str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner)null);
        }
        catch (Exception exception)
        {
            ;
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, par1, par2))
                {
                    this.selectedButton = guibutton;
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    // CHANGE
                    if (guibutton.id==199&&this instanceof GuiControls)mc.displayGuiScreen(new GuiSprint(this));
                    else actionPerformed(guibutton);
                    // END
                }
            }
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        if (this.selectedButton != null && par3 == 0)
        {
            this.selectedButton.mouseReleased(par1, par2);
            this.selectedButton = null;
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(int par1, int par2, int par3, long par4) {}

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {}

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3)
    {
        this.mc = par1Minecraft;
        this.fontRenderer = par1Minecraft.fontRenderer;
        this.width = par2;
        this.height = par3;
        this.buttonList.clear();
        // CHANGE
  		if(!hasChanged&&mc!=null){
  			GuiSprint.detectPlayerClassEdited();
  			GuiSprint.loadSettingBehavior(mc);
  			GuiSprint.loadSprint(mc);
  			KeyBinding.resetKeyBindingArrayAndHash();
  			hasChanged=true;
  		}
  		// END
        this.initGui();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {}

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput()
    {
        while (Mouse.next())
        {
            this.handleMouseInput();
        }

        while (Keyboard.next())
        {
            this.handleKeyboardInput();
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Minecraft.field_142025_a && k == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)))
        {
            k = 1;
        }

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.field_92018_d++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.field_92018_d > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseMovedOrUp(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState())
        {
            int i = Keyboard.getEventKey();
            char c0 = Keyboard.getEventCharacter();

            if (i == 87)
            {
                this.mc.toggleFullscreen();
                return;
            }

            this.keyTyped(c0, i);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen(){if(mc!=null)GuiSprint.updateSettingBehavior(mc);} // CHANGED LINE

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {}

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
        // CHANGE
        if ((width!=prevWidth||height!=prevHeight)&&this instanceof GuiControls){
        	int var3=mc.gameSettings.keyBindings.length;
        	int var4=24*(1+((Math.max(0,var3-14)+1)>>1));
        	if (!fromBs){
        		boolean isForge=false;
        		int newY=-1;
	        	for(Object o:buttonList){
	        		if (o instanceof GuiButton){
	        			GuiButton btn=(GuiButton)o;
	        			if (btn.id==200){
	        				if (btn.yPosition==height/6+168)btn.yPosition+=var4;
	        				else{
	        					isForge=true;
	        					btn.width=150;
	        					btn.xPosition=width/2+5;
	        					newY=btn.yPosition;
	        				}
	        			}
	        		}
	        	}
				if (!isForge)buttonList.add(new GuiButton(199,width/2-100,height/6+146+var4,"Better Sprinting")); // 168-22
				else buttonList.add(new GuiButton(199,width/2-155,newY,150,20,"Better Sprinting"));
        	}
			prevWidth=width; prevHeight=height;
        }
        // END
    }

    public void drawWorldBackground(int par1)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.drawBackground(par1);
        }
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int par1)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.mc.func_110434_K().func_110577_a(field_110325_k);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / f + (float)par1));
        tessellator.addVertexWithUV((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / f), (double)((float)this.height / f + (float)par1));
        tessellator.addVertexWithUV((double)this.width, 0.0D, 0.0D, (double)((float)this.width / f), (double)par1);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)par1);
        tessellator.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean par1, int par2) {}

    public static boolean isCtrlKeyDown()
    {
        return Minecraft.field_142025_a ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
