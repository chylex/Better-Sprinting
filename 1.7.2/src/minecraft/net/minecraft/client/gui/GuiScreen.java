package net.minecraft.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiScreen extends Gui
{
    public static RenderItem field_146296_j = new RenderItem();
    public Minecraft field_146297_k;
    public int field_146294_l;
    public int field_146295_m;
    public List field_146292_n = new ArrayList();
    public List field_146293_o = new ArrayList();
    public boolean field_146291_p;
    public FontRenderer field_146289_q;
    public GuiButton field_146290_a;
    public int field_146287_f;
    public long field_146288_g;
    public int field_146298_h;
    
    // CHANGE + publicized everything
    public static boolean held = false, hasChanged = false, fromBs = false;
    public static int stoptime = 0;
    
    public static boolean canRunInAllDirs(Minecraft mc){
    	return GuiSprint.disableModFunctionality?false:(mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||GuiSprint.svRunInAllDirs);
    }
    
    public static boolean canBoostFlying(Minecraft mc){
    	return GuiSprint.disableModFunctionality?false:(mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||mc.thePlayer.capabilities.isCreativeMode||GuiSprint.svFlyingBoost);
    }
    
    public int prevWidth=0, prevHeight=0;
    // END
    
    private static final String __OBFID = "CL_00000710";

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        int var4;

        for (var4 = 0; var4 < this.field_146292_n.size(); ++var4)
        {
            ((GuiButton)this.field_146292_n.get(var4)).func_146112_a(this.field_146297_k, par1, par2);
        }

        for (var4 = 0; var4 < this.field_146293_o.size(); ++var4)
        {
            ((GuiLabel)this.field_146293_o.get(var4)).func_146159_a(this.field_146297_k, par1, par2);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.field_146297_k.func_147108_a((GuiScreen)null);
            this.field_146297_k.setIngameFocus();
        }
    }

    public static String func_146277_j()
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

    public static void func_146275_d(String p_146275_0_)
    {
        try
        {
            StringSelection var1 = new StringSelection(p_146275_0_);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
        }
        catch (Exception var2)
        {
            ;
        }
    }

    protected void func_146285_a(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_)
    {
        List var4 = p_146285_1_.getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips);

        for (int var5 = 0; var5 < var4.size(); ++var5)
        {
            if (var5 == 0)
            {
                var4.set(var5, p_146285_1_.getRarity().rarityColor + (String)var4.get(var5));
            }
            else
            {
                var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
            }
        }

        this.func_146283_a(var4, p_146285_2_, p_146285_3_);
    }

    protected void func_146279_a(String p_146279_1_, int p_146279_2_, int p_146279_3_)
    {
        this.func_146283_a(Arrays.asList(new String[] {p_146279_1_}), p_146279_2_, p_146279_3_);
    }

    protected void func_146283_a(List p_146283_1_, int p_146283_2_, int p_146283_3_)
    {
        if (!p_146283_1_.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int var4 = 0;
            Iterator var5 = p_146283_1_.iterator();

            while (var5.hasNext())
            {
                String var6 = (String)var5.next();
                int var7 = this.field_146289_q.getStringWidth(var6);

                if (var7 > var4)
                {
                    var4 = var7;
                }
            }

            int var14 = p_146283_2_ + 12;
            int var15 = p_146283_3_ - 12;
            int var8 = 8;

            if (p_146283_1_.size() > 1)
            {
                var8 += 2 + (p_146283_1_.size() - 1) * 10;
            }

            if (var14 + var4 > this.field_146294_l)
            {
                var14 -= 28 + var4;
            }

            if (var15 + var8 + 6 > this.field_146295_m)
            {
                var15 = this.field_146295_m - var8 - 6;
            }

            this.zLevel = 300.0F;
            field_146296_j.zLevel = 300.0F;
            int var9 = -267386864;
            this.drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
            this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
            int var10 = 1347420415;
            int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
            this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
            this.drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
            this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

            for (int var12 = 0; var12 < p_146283_1_.size(); ++var12)
            {
                String var13 = (String)p_146283_1_.get(var12);
                this.field_146289_q.drawStringWithShadow(var13, var14, var15, -1);

                if (var12 == 0)
                {
                    var15 += 2;
                }

                var15 += 10;
            }

            this.zLevel = 0.0F;
            field_146296_j.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            for (int var4 = 0; var4 < this.field_146292_n.size(); ++var4)
            {
                GuiButton var5 = (GuiButton)this.field_146292_n.get(var4);

                if (var5.func_146116_c(this.field_146297_k, par1, par2))
                {
                    this.field_146290_a = var5;
                    var5.func_146113_a(this.field_146297_k.func_147118_V());
                    // CHANGE
                    if (var5.field_146127_k==205&&this instanceof GuiControls)field_146297_k.func_147108_a(new GuiSprint(this));
                    else func_146284_a(var5);
                    // END
                }
            }
        }
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (this.field_146290_a != null && p_146286_3_ == 0)
        {
            this.field_146290_a.func_146118_a(p_146286_1_, p_146286_2_);
            this.field_146290_a = null;
        }
    }

    protected void func_146273_a(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_) {}

    protected void func_146284_a(GuiButton p_146284_1_) {}

    public void func_146280_a(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_)
    {
        this.field_146297_k = p_146280_1_;
        this.field_146289_q = p_146280_1_.fontRenderer;
        this.field_146294_l = p_146280_2_;
        this.field_146295_m = p_146280_3_;
        this.field_146292_n.clear();
        // CHANGE
  		if(!hasChanged&&field_146297_k!=null){
  			GuiSprint.doInitialSetup(field_146297_k);
  			GuiSprint.loadSprint(field_146297_k);
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

    public void func_146269_k()
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.func_146274_d();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.func_146282_l();
            }
        }
    }

    public void func_146274_d()
    {
        int var1 = Mouse.getEventX() * this.field_146294_l / this.field_146297_k.displayWidth;
        int var2 = this.field_146295_m - Mouse.getEventY() * this.field_146295_m / this.field_146297_k.displayHeight - 1;
        int var3 = Mouse.getEventButton();

        if (Minecraft.isRunningOnMac && var3 == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)))
        {
            var3 = 1;
        }

        if (Mouse.getEventButtonState())
        {
            if (this.field_146297_k.gameSettings.touchscreen && this.field_146298_h++ > 0)
            {
                return;
            }

            this.field_146287_f = var3;
            this.field_146288_g = Minecraft.getSystemTime();
            this.mouseClicked(var1, var2, this.field_146287_f);
        }
        else if (var3 != -1)
        {
            if (this.field_146297_k.gameSettings.touchscreen && --this.field_146298_h > 0)
            {
                return;
            }

            this.field_146287_f = -1;
            this.func_146286_b(var1, var2, var3);
        }
        else if (this.field_146287_f != -1 && this.field_146288_g > 0L)
        {
            long var4 = Minecraft.getSystemTime() - this.field_146288_g;
            this.func_146273_a(var1, var2, this.field_146287_f, var4);
        }
    }

    public void func_146282_l()
    {
        if (Keyboard.getEventKeyState())
        {
            int var1 = Keyboard.getEventKey();
            char var2 = Keyboard.getEventCharacter();

            if (var1 == 87)
            {
                this.field_146297_k.toggleFullscreen();
                return;
            }

            this.keyTyped(var2, var1);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {if(field_146297_k!=null)GuiSprint.updateSettingBehavior(field_146297_k);} // CHANGED LINE

    public void func_146281_b() {}

    public void func_146276_q_()
    {
        this.func_146270_b(0);
        // CHANGE
        if ((field_146294_l!=prevWidth||field_146295_m!=prevHeight)&&this instanceof GuiControls){
        	if (!fromBs)field_146292_n.add(0,new GuiButton(205,field_146294_l/2+5,18+24,150,20,"Better Sprinting"));
			prevWidth=field_146294_l; prevHeight=field_146295_m;
        }
        // END
    }

    public void func_146270_b(int p_146270_1_)
    {
        if (this.field_146297_k.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.field_146294_l, this.field_146295_m, -1072689136, -804253680);
        }
        else
        {
            this.func_146278_c(p_146270_1_);
        }
    }

    public void func_146278_c(int p_146278_1_)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var2 = Tessellator.instance;
        this.field_146297_k.getTextureManager().bindTexture(optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var3 = 32.0F;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(4210752);
        var2.addVertexWithUV(0.0D, (double)this.field_146295_m, 0.0D, 0.0D, (double)((float)this.field_146295_m / var3 + (float)p_146278_1_));
        var2.addVertexWithUV((double)this.field_146294_l, (double)this.field_146295_m, 0.0D, (double)((float)this.field_146294_l / var3), (double)((float)this.field_146295_m / var3 + (float)p_146278_1_));
        var2.addVertexWithUV((double)this.field_146294_l, 0.0D, 0.0D, (double)((float)this.field_146294_l / var3), (double)p_146278_1_);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)p_146278_1_);
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

    public static boolean func_146271_m()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean func_146272_n()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
