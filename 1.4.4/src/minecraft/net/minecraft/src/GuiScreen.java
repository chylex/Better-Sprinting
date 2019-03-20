package net.minecraft.src;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui
{
    public static final boolean field_90017_e = Minecraft.getOs() == EnumOS.MACOS;

    /** Reference to the Minecraft object. */
    protected Minecraft mc;

    /** The width of the screen object. */
    public int width;

    /** The height of the screen object. */
    public int height;

    /** A list of all the controls added to this container. */
    protected List controlList = new ArrayList();
    public boolean allowUserInput = false;

    /** The FontRenderer used by GuiScreen */
    protected FontRenderer fontRenderer;
    public GuiParticle guiParticles;

    /** The button that was just pressed. */
    private GuiButton selectedButton = null;
    private int field_85042_b = 0;
    private long field_85043_c = 0L;
    
    // CHANGE
    public static KeyBinding keyBindSprint = new KeyBinding("Sprint (hold)",29);
    public static KeyBinding keyBindSprintToggle = new KeyBinding("Sprint (toggle)",34);
    public static KeyBinding keyBindSneakToggle = new KeyBinding("Sneak (toggle)",21);
    public static boolean allowDoubleTap = false;
    public static boolean held = false;
    public static int stoptime = 0;
    public static boolean hasChanged = false;
    
    protected void saveSprint(){
    	try{
    		java.io.File file = new java.io.File(mc.mcDataDir,"sprint.txt");
            java.io.PrintWriter printwriter = new java.io.PrintWriter(new java.io.FileWriter(file));
            printwriter.println((new StringBuilder()).append(GuiScreen.keyBindSprint.keyCode).append("|").append(GuiScreen.keyBindSprintToggle.keyCode).append("|").append(GuiScreen.keyBindSneakToggle.keyCode).append("|").append(GuiScreen.allowDoubleTap?1:0).toString());
            printwriter.close();
        }
        catch (Exception exception){
            System.out.println("Failed to save options");
            exception.printStackTrace();
        }
    }
    // END

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        for (int var4 = 0; var4 < this.controlList.size(); ++var4)
        {
            GuiButton var5 = (GuiButton)this.controlList.get(var4);
            var5.drawButton(this.mc, par1, par2);
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
            Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

            if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String)var0.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception var1)
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
            StringSelection var1 = new StringSelection(par0Str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
        }
        catch (Exception var2)
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
            for (int var4 = 0; var4 < this.controlList.size(); ++var4)
            {
                GuiButton var5 = (GuiButton)this.controlList.get(var4);

                if (var5.mousePressed(this.mc, par1, par2))
                {
                    this.selectedButton = var5;
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    this.actionPerformed(var5);
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

    protected void func_85041_a(int par1, int par2, int par3, long par4) {}

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
        this.guiParticles = new GuiParticle(par1Minecraft);
        this.mc = par1Minecraft;
        this.fontRenderer = par1Minecraft.fontRenderer;
        this.width = par2;
        this.height = par3;
        this.controlList.clear();
        // CHANGE
  		if(!hasChanged&&mc!=null){
  			try{
  				java.io.File optionsFile=new java.io.File(mc.mcDataDir,"sprint.txt");
  				if(!optionsFile.exists()){
  					throw new Exception();
  				}
  				java.io.BufferedReader bufferedreader=new java.io.BufferedReader(new java.io.FileReader(optionsFile));
  				for(String s="";(s=bufferedreader.readLine())!=null;){
  					try{
  						//System.out.println("BSM "+s);
  						String[] str=null;
  						try{
  							str=s.split("\\|");
  						}catch(Exception exception){
  							str[0]=s;
  						}
  						if (str!=null){
  							if (str.length>=1)GuiScreen.keyBindSprint.keyCode=Integer.parseInt(str[0]);
  							if (str.length>=2)GuiScreen.keyBindSprintToggle.keyCode=Integer.parseInt(str[1]);
  							if (str.length>=3)GuiScreen.keyBindSneakToggle.keyCode=Integer.parseInt(str[2]);
  							if (str.length>=4)GuiScreen.allowDoubleTap=Integer.parseInt(str[3])==1;
  						}
  					}catch(Exception exception1){exception1.printStackTrace();
  						System.out.println((new StringBuilder()).append("Skipping bad option: ").append(s).toString());
  					}
  				}
  				KeyBinding.resetKeyBindingArrayAndHash();
  				bufferedreader.close();
  			}
  	        catch (Exception exception){}
  			KeyBinding[] append={GuiScreen.keyBindSneakToggle,GuiScreen.keyBindSprint,GuiScreen.keyBindSprintToggle};
  			KeyBinding[] tmp=new KeyBinding[mc.gameSettings.keyBindings.length+append.length];
  			System.arraycopy(mc.gameSettings.keyBindings,0,tmp,0,mc.gameSettings.keyBindings.length);
  			System.arraycopy(append,0,tmp,mc.gameSettings.keyBindings.length,append.length);
  			mc.gameSettings.keyBindings=tmp;
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
        int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (Mouse.getEventButtonState())
        {
            this.field_85042_b = Mouse.getEventButton();
            this.field_85043_c = Minecraft.getSystemTime();
            this.mouseClicked(var1, var2, this.field_85042_b);
            if (this instanceof GuiControls)saveSprint(); // CHANGED LINE
        }
        else if (Mouse.getEventButton() != -1)
        {
            this.field_85042_b = -1;
            this.mouseMovedOrUp(var1, var2, Mouse.getEventButton());
        }
        else if (this.mc.gameSettings.field_85185_A && this.field_85042_b != -1 && this.field_85043_c > 0L)
        {
            long var3 = Minecraft.getSystemTime() - this.field_85043_c;
            this.func_85041_a(var1, var2, this.field_85042_b, var3);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState())
        {
            int var1 = Keyboard.getEventKey();
            char var2 = Keyboard.getEventCharacter();

            if (var1 == 87)
            {
                this.mc.toggleFullscreen();
                return;
            }

            if (field_90017_e && var1 == 28 && var2 == 0)
            {
                var1 = 29;
            }

            this.keyTyped(var2, var1);
            if (this instanceof GuiControls)saveSprint(); // CHANGED LINE
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

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
        Tessellator var2 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var3 = 32.0F;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(4210752);
        var2.addVertexWithUV(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / var3 + (float)par1));
        var2.addVertexWithUV((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / var3), (double)((float)this.height / var3 + (float)par1));
        var2.addVertexWithUV((double)this.width, 0.0D, 0.0D, (double)((float)this.width / var3), (double)par1);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)par1);
        var2.draw();
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
        boolean var0 = Keyboard.isKeyDown(28) && Keyboard.getEventCharacter() == 0;
        return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157) || field_90017_e && (var0 || Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220));
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
