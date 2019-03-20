package net.minecraft.src;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import net.minecraft.client.Minecraft;

public class GuiControls extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle;

    /** Reference to the GameSettings object. */
    private GameSettings options;

    /** The ID of the  button that has been pressed. */
    private int buttonId;

    public GuiControls(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        screenTitle = "Controls";
        buttonId = -1;
        parentScreen = par1GuiScreen;
        options = par2GameSettings;
    }

    private int func_20080_j()
    {
        return width / 2 - 155;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        int i = func_20080_j();

        for (int j = 0; j < options.keyBindings.length; j++)
        {
            controlList.add(new GuiSmallButton(j, i + (j % 2) * 160, height / 6 + 24 * (j >> 1) - 14, 70, 20, options.getOptionDisplayString(j))); // CHANGED LINE
        }

        controlList.add(new GuiButton(200, width / 2 - 100, height / 6 + 178, stringtranslate.translateKey("gui.done"))); // CHANGED LINE
        screenTitle = stringtranslate.translateKey("controls.title");
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        for (int i = 0; i < options.keyBindings.length; i++)
        {
            ((GuiButton)controlList.get(i)).displayString = options.getOptionDisplayString(i);
        }

        if (par1GuiButton.id == 200)
        {
            mc.displayGuiScreen(parentScreen);
        }
        else
        {
            buttonId = par1GuiButton.id;
            par1GuiButton.displayString = (new StringBuilder()).append("> ").append(options.getOptionDisplayString(par1GuiButton.id)).append(" <").toString();
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (buttonId >= 0)
        {
            options.setKeyBinding(buttonId, -100 + par3);
            ((GuiButton)controlList.get(buttonId)).displayString = options.getOptionDisplayString(buttonId);
            buttonId = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.mouseClicked(par1, par2, par3);
        }
        saveSprint(); // CHANGED LINE
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (buttonId >= 0)
        {
            options.setKeyBinding(buttonId, par2);
            ((GuiButton)controlList.get(buttonId)).displayString = options.getOptionDisplayString(buttonId);
            buttonId = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
        saveSprint(); // CHANGED LINE
    }
    
    // CHANGE
    void saveSprint(){
    	try{
    		java.io.File file = new java.io.File(mc.mcDataDir,"sprint.txt");
            PrintWriter printwriter = new PrintWriter(new FileWriter(file));
            printwriter.println((new StringBuilder()).append(GuiScreen.keyBindSprint.keyCode).append("|").append(GuiScreen.keyBindSprintToggle.keyCode).append("|").append(GuiScreen.keyBindSneakToggle.keyCode).toString());
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
        drawDefaultBackground();
        drawCenteredString(fontRenderer, screenTitle, width / 2, 12, 0xffffff); // CHANGED LINE
        int i = func_20080_j();

        for (int j = 0; j < options.keyBindings.length; j++)
        {
            boolean flag = false;
            int k = 0;

            do
            {
                if (k >= options.keyBindings.length)
                {
                    break;
                }

                if (k != j && options.keyBindings[j].keyCode == options.keyBindings[k].keyCode)
                {
                    flag = true;
                    break;
                }

                k++;
            }
            while (true);

            k = j;

            if (buttonId == j)
            {
                ((GuiButton)controlList.get(k)).displayString = "\247f> \247e??? \247f<";
            }
            else if (flag)
            {
                ((GuiButton)controlList.get(k)).displayString = (new StringBuilder()).append("\247c").append(options.getOptionDisplayString(k)).toString();
            }
            else
            {
                ((GuiButton)controlList.get(k)).displayString = options.getOptionDisplayString(k);
            }

            drawString(fontRenderer, options.getKeyBindingDescription(j), i + (j % 2) * 160 + 70 + 6, height / 6 + 24 * (j >> 1) - 7, -1); // CHANGED LINE
        }

        super.drawScreen(par1, par2, par3);
    }
}
