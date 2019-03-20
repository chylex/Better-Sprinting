package net.minecraft.client.gui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovementInputFromOptions;

public class GuiSprint extends GuiScreen{
	public static KeyBinding keyBindSprint = new KeyBinding("Sprint (hold)",29,"key.categories.movement");
    public static KeyBinding keyBindSprintToggle = new KeyBinding("Sprint (toggle)",34,"key.categories.movement");
    public static KeyBinding keyBindSneakToggle = new KeyBinding("Sneak (toggle)",21,"key.categories.movement");
    public static KeyBinding keyBindSprintMenu = new KeyBinding("Sprint menu",24,"key.categories.movement");
    public static int flyingBoost = 3;
    public static boolean allowDoubleTap = false;
    public static boolean allowAllDirs = false;
    public static boolean disableModFunctionality = false;
    public static boolean showedToggleSneakWarning = false;
    
    private static int nbtInt(NBTTagCompound tag, String key, int def){ return tag.hasKey(key)?tag.getInteger(key):def; }
    private static boolean nbtBool(NBTTagCompound tag, String key, boolean def){ return tag.hasKey(key)?tag.getBoolean(key):def; }
    
    public static void loadSprint(Minecraft mc){
    	File file = new java.io.File(mc.mcDataDir,"sprint.nbt");
    	if (!file.exists())return;
    	try{
    		NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(file)).getCompoundTag("Data");
    		if (tag==null)return;
    		keyBindSprintMenu.func_151462_b(nbtInt(tag,"keyMenu",24));
    		keyBindSprint.func_151462_b(nbtInt(tag,"keySprint",29));
    		keyBindSprintToggle.func_151462_b(nbtInt(tag,"keySprintToggle",34));
    		keyBindSneakToggle.func_151462_b(nbtInt(tag,"keySneakToggle",21));
    		flyingBoost=nbtInt(tag,"flyBoost",3);
    		allowDoubleTap=nbtBool(tag,"doubleTap",false);
    		allowAllDirs=nbtBool(tag,"allDirs",false);
    		disableModFunctionality=nbtBool(tag,"disableMod",false);
    		showedToggleSneakWarning=nbtBool(tag,"showedWarn",false);
    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println("Error loading Better Sprinting settings!");
    	}
    	updateSettingBehavior(mc);
    }
    
    public static void saveSprint(Minecraft mc){
    	NBTTagCompound tag=new NBTTagCompound();
    	tag.setInteger("keyMenu",keyBindSprintMenu.func_151463_i());
    	tag.setInteger("keySprint",keyBindSprint.func_151463_i());
    	tag.setInteger("keySprintToggle",keyBindSprintToggle.func_151463_i());
    	tag.setInteger("keySneakToggle",keyBindSneakToggle.func_151463_i());
    	tag.setInteger("flyBoost",flyingBoost);
    	tag.setBoolean("doubleTap",allowDoubleTap);
    	tag.setBoolean("allDirs",allowAllDirs);
    	tag.setBoolean("disableMod",disableModFunctionality);
    	tag.setBoolean("showedWarn",showedToggleSneakWarning);
    	NBTTagCompound fintag=new NBTTagCompound();
    	fintag.setTag("Data",tag);
    	try{
    		CompressedStreamTools.writeCompressed(fintag,new FileOutputStream(new File(mc.mcDataDir,"sprint.nbt")));
    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println("Error saving Better Sprinting settings!");
    	}
    }
	
	private static boolean isPlayerClassEdited = false;
	public static boolean svFlyingBoost = false, svRunInAllDirs = false;
	private static boolean shouldRestoreSneakToggle = false;
	public static String connectedServer = "";
	private static byte connectedServerResponse = 0;
	private static byte behaviorCheckTimer = 10;
	
	public static void doInitialSetup(Minecraft mc){
		try{
			EntityPlayerSP.class.getDeclaredField("BetterSprintingClassCheck");
		}catch(Exception e){
			isPlayerClassEdited=true;
		}
		
		GameSettings settings=mc.gameSettings;
		KeyBinding[] newBinds=new KeyBinding[settings.keyBindings.length-1];
		for(int a=0,index=0; a<settings.keyBindings.length; a++){
			if (settings.keyBindings[a]!=settings.field_151444_V)newBinds[index++]=settings.keyBindings[a];
		}
		settings.keyBindings=newBinds;
		settings.field_151444_V.func_151462_b(0);
		KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	public static void updateSettingBehavior(Minecraft mc){
		if (mc.currentScreen!=null&&mc.thePlayer!=null&&mc.thePlayer.isSneaking()){
			MovementInputFromOptions opts=(MovementInputFromOptions)((EntityPlayerSP)mc.thePlayer).movementInput;
			
			if (opts.sneakToggle&&!(mc.currentScreen instanceof GuiGameOver)){
				if (!showedToggleSneakWarning){
					mc.thePlayer.func_145747_a(new ChatComponentText("First-time warning: You can open inventories and menus while sneaking, however you will not be sneaking for the time it is open. Once you close the menu, sneaking will be restored."));
					mc.func_147108_a(null);
					mc.setIngameFocus();
					showedToggleSneakWarning=true;
					saveSprint(mc);
				}
				else{
					shouldRestoreSneakToggle=true;
					opts.sneakToggle=false;
				}
			}
		}
		
		if (shouldRestoreSneakToggle&&mc.currentScreen==null){
			MovementInputFromOptions opts=(MovementInputFromOptions)((EntityPlayerSP)mc.thePlayer).movementInput;
			opts.sneakToggle=true;
			shouldRestoreSneakToggle=false;
		}

		if (behaviorCheckTimer>0){
			--behaviorCheckTimer;
			return;
		}
		behaviorCheckTimer = 10;
		
		if (mc.thePlayer==null){
			connectedServer="";
			svFlyingBoost=svRunInAllDirs=false;
		}
		else if (!mc.isIntegratedServerRunning()&&mc.func_147104_D()!=null&&!disableModFunctionality){
			String serverIP=mc.func_147104_D().serverIP;
			
			if (!connectedServer.equals(serverIP)){
				svFlyingBoost=svRunInAllDirs=false;
				connectedServer=new String(serverIP);
				
				if ((connectedServer.startsWith("127.0.0.1")||connectedServer.equals("localhost"))==false){
					mc.thePlayer.sendQueue.func_147297_a(new C17PacketCustomPayload("BSprint",new byte[]{ 4 }));
					connectedServerResponse=4;
				}
				else svFlyingBoost=svRunInAllDirs=true;
			}
			else if (connectedServerResponse>0){
				--connectedServerResponse;
				/*GuiNewChat chat=mc.ingameGUI.func_146158_b();
				byte reflectionList=1;
				List<ChatLine> chatLines=new ArrayList<ChatLine>(6);
				
				try{
					for(Field field:GuiNewChat.class.getDeclaredFields()){
						field.setAccessible(true);
						Object o=field.get(chat);
						if (o instanceof List){
							if (--reflectionList==0)continue;
							List list=(List)o;
							for(int a=0; a<Math.min(list.size(),6); a++){
								if (list.get(a) instanceof ChatLine)chatLines.add((ChatLine)list.get(a));
							}
						}
					}
				}catch(Exception e){}
				
				for(ChatLine chatLine:chatLines){
					String str="";// TODO

					if (str.startsWith("\u00a7b\u00a7r\u00a74\u00a7")){
						String[] set=str.substring(7).split("\u00a7");
						for(int a=0; a<set.length; a++){
							if (!set[a].equals("1"))continue;
							if (a==0)svFlyingBoost=true;
							else if (a==1)svRunInAllDirs=true;
							
						}
						connectedServerResponse=0;
						chat.func_146242_c(chatLine.getChatLineID());
						break;
					}
				}*/
			}
		}
	}
	
	// GUI
	
	private GuiScreen parentScreen;
	private int buttonId = -1;
	private GuiButton btnDoubleTap,btnFlyBoost,btnAllDirs,btnDisableMod;
	protected KeyBinding[] sprintBindings=new KeyBinding[]{
		keyBindSprint, keyBindSprintToggle, keyBindSneakToggle, keyBindSprintMenu
	};
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen=parentScreen;
	}

	@Override
	public void initGui(){
		field_146292_n.clear();
		
		GuiScreen.fromBs = false;
        int left=getLeftColumnX(), ypos=0;

        for(int a=0; a<sprintBindings.length; ++a){
			ypos=field_146295_m/6+24*(a>>1);
			GuiOptionButton btn=new GuiOptionButton(a,left+a%2*160,ypos,70,20,getKeyCodeString(a));
			field_146292_n.add(btn);
			if ((a==1||a==2)&&disableModFunctionality)btn.field_146124_l=false;
		}
        
        ypos+=48;
        btnDoubleTap=new GuiButton(199,left,ypos,70,20,""); field_146292_n.add(btnDoubleTap);
        if (disableModFunctionality)btnDoubleTap.field_146124_l=false;
        
        btnAllDirs=new GuiButton(198,left+160,ypos,70,20,""); field_146292_n.add(btnAllDirs);
        if (!GuiScreen.canRunInAllDirs(field_146297_k))btnAllDirs.field_146124_l=false;
        
        ypos+=24;
        btnFlyBoost=new GuiButton(197,left,ypos,70,20,""); field_146292_n.add(btnFlyBoost);
        if (!GuiScreen.canBoostFlying(field_146297_k))btnFlyBoost.field_146124_l=false;
        
        btnDisableMod=new GuiButton(196,left+160,ypos,70,20,""); field_146292_n.add(btnDisableMod);
        if (!(field_146297_k.thePlayer==null&&field_146297_k.theWorld==null))btnDisableMod.field_146124_l=false;
        
        field_146292_n.add(new GuiButton(200,field_146294_l/2-100,field_146295_m/6+168,parentScreen==null?98:200,20,I18n.getStringParams("gui.done")));
        if (parentScreen==null)field_146292_n.add(new GuiButton(190,field_146294_l/2+2,field_146295_m/6+168,98,20,I18n.getStringParams("options.controls")));
        updateButtons();
    }
	
	private void updateButtons(){
		btnDoubleTap.field_146126_j=disableModFunctionality?"Unavailable":(allowDoubleTap?"Enabled":"Disabled");
		btnFlyBoost.field_146126_j=GuiScreen.canBoostFlying(field_146297_k)?(flyingBoost==0?"Disabled":(flyingBoost+1)+"x"):"Unavailable";
		btnAllDirs.field_146126_j=GuiScreen.canRunInAllDirs(field_146297_k)?(allowAllDirs?"Enabled":"Disabled"):"Unavailable";
		btnDisableMod.field_146126_j=disableModFunctionality?"Yes":"No";
	}

	@Override
	protected void func_146284_a(GuiButton btn){
		for(int var2=0; var2<sprintBindings.length; ++var2){
			((GuiButton)field_146292_n.get(var2)).field_146126_j=getKeyCodeString(var2);
		}

		switch(btn.field_146127_k){
			case 190:
				GuiScreen.fromBs=true;
				field_146297_k.func_147108_a(new GuiControls(this,field_146297_k.gameSettings));
				break;
				
			case 196:
				if (field_146297_k.thePlayer==null&&field_146297_k.theWorld==null){
					disableModFunctionality=!disableModFunctionality;
					initGui();
				}
				break;
				
			case 197:
				if (GuiScreen.canBoostFlying(field_146297_k)&&++flyingBoost==8)flyingBoost=0;
				break;
				
			case 198:
				if (GuiScreen.canRunInAllDirs(field_146297_k))allowAllDirs=!allowAllDirs;
				break;
				
			case 199:
				if (!disableModFunctionality)allowDoubleTap=!allowDoubleTap;
				break;
				
			case 200:
				if (parentScreen==null){
					field_146297_k.func_147108_a((GuiScreen)null);
	                field_146297_k.setIngameFocus();
				}
				else{
					field_146297_k.func_147108_a(parentScreen);
					parentScreen.prevWidth=0;
				}
				break;
				
			default:
				buttonId=btn.field_146127_k;
				btn.field_146126_j="> "+field_146297_k.gameSettings.getKeyDisplayString(field_146297_k.gameSettings.keyBindings[btn.field_146127_k].func_151463_i())+" <";
		}
		
		saveSprint(field_146297_k);
		updateButtons();
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3){
		if (!handleInput(-100+par3))super.mouseClicked(par1,par2,par3);
	}

	@Override
	protected void keyTyped(char par1, int par2){
		if (!handleInput(par2))super.keyTyped(par1,par2);
	}
	
	private boolean handleInput(int par1){
		if (buttonId>=0&&buttonId<180){
			sprintBindings[buttonId].func_151462_b(par1);
			((GuiButton)field_146292_n.get(buttonId)).field_146126_j=getKeyCodeString(buttonId);
			buttonId=-1;
			KeyBinding.resetKeyBindingArrayAndHash();
			return true;
		}
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3){
		func_146276_q_();
		drawCenteredString(field_146289_q,"Better Sprinting",field_146294_l/2,20,16777215);

		int left=getLeftColumnX(),a=0;
		while(a<sprintBindings.length){
			boolean alreadyUsed=false;
			int b=0;

			while(true){
				if (b<sprintBindings.length){
					if (b==a||sprintBindings[a].func_151463_i()!=sprintBindings[b].func_151463_i()){
						++b;
						continue;
					}
					alreadyUsed=true;
				}
				
				for(int i=0; i<field_146297_k.gameSettings.keyBindings.length; i++){
					if (sprintBindings[a].func_151463_i()==field_146297_k.gameSettings.keyBindings[i].func_151463_i()){
						alreadyUsed=true;
						break;
					}
				}

				if (buttonId==a)((GuiButton)field_146292_n.get(a)).field_146126_j="\u00a7f> \u00a7e??? \u00a7f<";
				else if (alreadyUsed)((GuiButton)field_146292_n.get(a)).field_146126_j="\u00a7c"+getKeyCodeString(a);
				else ((GuiButton)field_146292_n.get(a)).field_146126_j=getKeyCodeString(a);

				drawString(field_146289_q,sprintBindings[a].func_151464_g(),left+a%2*160+70+6,field_146295_m/6+24*(a>>1)+7,-1);
				++a;
				break;
			}
		}

		drawButtonTitle("Double tapping",btnDoubleTap);
		drawButtonTitle("Run in all directions",btnAllDirs);
		drawButtonTitle("Flying boost",btnFlyBoost);
		drawButtonTitle("Disable mod functionality",btnDisableMod);
		
		for(int i=0; i<field_146292_n.size(); i++){
			GuiButton btn=(GuiButton)field_146292_n.get(i);
			if (mouseX>=btn.field_146128_h&&mouseX<=btn.field_146128_h+btn.field_146120_f&&
				mouseY>=btn.field_146129_i&&mouseY<=btn.field_146129_i+btn.field_146121_g){
				String info="";
				
				switch(i){
					case 0: info="Hold to sprint."; break;
					case 1: info="Press once to start or stop sprinting."; break;
					case 2: info="Press once to start or stop sneaking.#When you open an inventory/menu, sneaking will stop and get restored when you close the menu."; break;
					case 3: info="Key to open this menu ingame."; break;
					case 4: info="Enable or disable sprinting by double-tapping the forward key."; break;
					case 5: info="Sprint in all directions.#You cannot use this in multiplayer unless the server allows it."; break;
					case 6: info="Press whilst flying in creative mode to fly faster.#Works in survival mode flying (modded game) if the server allows it."; break;
					case 7: info="Disables all non-vanilla functionality of Better Sprinting.#This option can be used if a server doesn't allow the mod."; break;
				}
				
				String[] spl=info.split("#");
				drawCenteredString(field_146289_q,spl[0],field_146294_l/2,field_146295_m/6+138,-1);
				if (spl.length==2)drawCenteredString(field_146289_q,spl[1],field_146294_l/2,field_146295_m/6+148,-1);
				break;
			}
		}
		
		if (isPlayerClassEdited)drawCenteredString(field_146289_q,"\u00a7cDetected conflicted class, vital mod functions may not work!",field_146294_l/2,30,-1);

		super.drawScreen(mouseX,mouseY,par3);
    }
	
	private int getLeftColumnX(){
        return field_146294_l/2-155;
    }
	
	private String getKeyCodeString(int i){
		return GameSettings.getKeyDisplayString(sprintBindings[i].func_151463_i());
	}
	
	private void drawButtonTitle(String title, GuiButton btn){
		drawString(field_146289_q,title,btn.field_146128_h+70+6,btn.field_146129_i+7,-1);
	}
}

// CHANGED (NEW) CLASS