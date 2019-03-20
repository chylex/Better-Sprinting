package net.minecraft.src;

public class GuiControls extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Controls";

    /** Reference to the GameSettings object. */
    private GameSettings options;

    /** The ID of the  button that has been pressed. */
    private int buttonId = -1;

    public GuiControls(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.parentScreen = par1GuiScreen;
        this.options = par2GameSettings;
    }

    private int func_73907_g()
    {
        return this.width / 2 - 155;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        int var2 = this.func_73907_g();

        // CHANGE
        int var3;
        for (var3 = 0; var3 < this.options.keyBindings.length; ++var3){
            controlList.add(new GuiSmallButton(var3, var2 + (var3 % 2) * 160, height / 6 + 22 * (var3 >> 1) - 17, 70, 20, options.getOptionDisplayString(var3)));
        }
        controlList.add(new GuiButton(200, width / 2 - 100, height / 6 + 204, var1.translateKey("gui.done")));
        controlList.add(new GuiSmallButton(199, var2 + (var3 % 2) * 160, height / 6 + 22 * (var3 >> 1) - 17, 70, 20, (GuiScreen.allowDoubleTap?"Yes":"No")));
        ++var3;
        controlList.add(new GuiSmallButton(198, var2 + (var3 % 2) * 160, height / 6 + 22 * (var3 >> 1) - 17, 70, 20, (GuiScreen.allowFlyingBoost?"On":"Off")));
        // END
        this.screenTitle = var1.translateKey("controls.title");
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
    	// CHANGE
    	if (par1GuiButton.id==199){
    		GuiScreen.allowDoubleTap=!GuiScreen.allowDoubleTap;
    		for (int var2 = 0; var2 < controlList.size(); ++var2){
    			GuiButton button=(GuiButton)controlList.get(var2);
    			if (button.id==199){
    				button.displayString=(GuiScreen.allowDoubleTap?"Yes":"No");
    				break;
    			}
            }
    		return;
    	}
    	else if (par1GuiButton.id==198){
    		GuiScreen.allowFlyingBoost=!GuiScreen.allowFlyingBoost;
    		for (int var2 = 0; var2 < controlList.size(); ++var2){
    			GuiButton button=(GuiButton)controlList.get(var2);
    			if (button.id==198){
    				button.displayString=(GuiScreen.allowFlyingBoost?"On":"Off");
    				break;
    			}
            }
    		return;
    	}
    	// END
        for (int var2 = 0; var2 < this.options.keyBindings.length; ++var2)
        {
            ((GuiButton)this.controlList.get(var2)).displayString = this.options.getOptionDisplayString(var2);
        }

        if (par1GuiButton.id == 200)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else
        {
            this.buttonId = par1GuiButton.id;
            par1GuiButton.displayString = "> " + this.options.getOptionDisplayString(par1GuiButton.id) + " <";
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (this.buttonId >= 0)
        {
            this.options.setKeyBinding(this.buttonId, -100 + par3);
            ((GuiButton)this.controlList.get(this.buttonId)).displayString = this.options.getOptionDisplayString(this.buttonId);
            this.buttonId = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.mouseClicked(par1, par2, par3);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (this.buttonId >= 0)
        {
            this.options.setKeyBinding(this.buttonId, par2);
            ((GuiButton)this.controlList.get(this.buttonId)).displayString = this.options.getOptionDisplayString(this.buttonId);
            this.buttonId = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        drawCenteredString(fontRenderer, screenTitle, width / 2, 10, 16777215); // CHANGED LINE
        int var4 = this.func_73907_g();
        int var5 = 0;

        while (var5 < this.options.keyBindings.length)
        {
            boolean var6 = false;
            int var7 = 0;

            while (true)
            {
                if (var7 < this.options.keyBindings.length)
                {
                    if (var7 == var5 || this.options.keyBindings[var5].keyCode != this.options.keyBindings[var7].keyCode)
                    {
                        ++var7;
                        continue;
                    }

                    var6 = true;
                }

                if (this.buttonId == var5)
                {
                    ((GuiButton)this.controlList.get(var5)).displayString = "\u00a7f> \u00a7e??? \u00a7f<";
                }
                else if (var6)
                {
                    ((GuiButton)this.controlList.get(var5)).displayString = "\u00a7c" + this.options.getOptionDisplayString(var5);
                }
                else
                {
                    ((GuiButton)this.controlList.get(var5)).displayString = this.options.getOptionDisplayString(var5);
                }

                drawString(fontRenderer, options.getKeyBindingDescription(var5), var4 + (var5 % 2) * 160 + 70 + 6, height / 6 + 22 * (var5 >> 1) - 10, -1); // CHANGED LINE
                ++var5;
                break;
            }
        }
        
        // CHANGE
        drawString(fontRenderer, "Allow double tap", var4 + (var5 % 2) * 160 + 70 + 6, height / 6 + 22 * (var5 >> 1) - 10, -1);
        ++var5;
        drawString(fontRenderer, "Flying boost", var4 + (var5 % 2) * 160 + 70 + 6, height / 6 + 22 * (var5 >> 1) - 10, -1);
        // END

        super.drawScreen(par1, par2, par3);
    }
}
