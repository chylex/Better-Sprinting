package net.minecraft.src;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GuiSprint extends GuiScreen{
	public static KeyBinding keyBindSprint = new KeyBinding("Sprint (hold)",29);
    public static KeyBinding keyBindSprintToggle = new KeyBinding("Sprint (toggle)",34);
    public static KeyBinding keyBindSneakToggle = new KeyBinding("Sneak (toggle)",21);
    public static KeyBinding keyBindSprintMenu = new KeyBinding("Sprint menu",24);
    public static int flyingBoost = 3;
    public static boolean allowDoubleTap = false;
    public static boolean allowAllDirs = false;
    
    private static int nbtInt(NBTTagCompound tag, String key, int def){ return tag.hasKey(key)?tag.getInteger(key):def; }
    private static boolean nbtBool(NBTTagCompound tag, String key, boolean def){ return tag.hasKey(key)?tag.getBoolean(key):def; }
    
    public static void loadSprint(Minecraft mc){
    	File file = new java.io.File(mc.mcDataDir,"sprint.nbt");
    	if (!file.exists())return;
    	try{
    		NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(file)).getCompoundTag("Data");
    		if (tag==null)return;
    		keyBindSprintMenu.keyCode=nbtInt(tag,"keyMenu",24);
    		keyBindSprint.keyCode=nbtInt(tag,"keySprint",29);
    		keyBindSprintToggle.keyCode=nbtInt(tag,"keySprintToggle",34);
    		keyBindSneakToggle.keyCode=nbtInt(tag,"keySneakToggle",21);
    		flyingBoost=nbtInt(tag,"flyBoost",3);
    		allowDoubleTap=nbtBool(tag,"doubleTap",false);
    		allowAllDirs=nbtBool(tag,"allDirs",false);
    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println("Error loading Better Sprinting settings!");
    	}
    	updateSettingBehavior(mc);
    }
    
    public static void saveSprint(Minecraft mc){
    	NBTTagCompound tag=new NBTTagCompound();
    	tag.setInteger("keyMenu",keyBindSprintMenu.keyCode);
    	tag.setInteger("keySprint",keyBindSprint.keyCode);
    	tag.setInteger("keySprintToggle",keyBindSprintToggle.keyCode);
    	tag.setInteger("keySneakToggle",keyBindSneakToggle.keyCode);
    	tag.setInteger("flyBoost",flyingBoost);
    	tag.setBoolean("doubleTap",allowDoubleTap);
    	tag.setBoolean("allDirs",allowAllDirs);
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
	public static String connectedServer = "";
	private static byte connectedServerResponse = 0;
	private static Field serverDataField;
	private static byte behaviorCheckTimer = 10;
	
	public static void detectPlayerClassEdited(){
		try{
			EntityPlayerSP.class.getDeclaredField("BetterSprintingClassCheck");
		}catch(Exception e){
			isPlayerClassEdited=true;
		}
	}
	
	public static void loadSettingBehavior(Minecraft mc){
		try{
			for(Field field:Minecraft.class.getDeclaredFields()){
				field.setAccessible(true);
				if (ServerData.class==field.getType()){
					serverDataField=field;
					System.out.println("Setting up server-side settings finished.");
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error setting up server-side settings!");
		}
	}
	
	public static void updateSettingBehavior(Minecraft mc){
		if (behaviorCheckTimer>0){
			--behaviorCheckTimer;
			return;
		}
		behaviorCheckTimer = 10;
		
		if (mc.thePlayer==null){
			connectedServer="";
			svFlyingBoost=svRunInAllDirs=false;
		}
		else if (!mc.isIntegratedServerRunning()&&serverDataField!=null){
			String serverIP="";
			try{
				serverIP=((ServerData)serverDataField.get(mc)).serverIP;
			}catch(Exception e){}
			
			if (!connectedServer.equals(serverIP)){
				svFlyingBoost=svRunInAllDirs=false;
				connectedServer=new String(serverIP);
				
				if ((connectedServer.startsWith("127.0.0.1")||connectedServer.equals("localhost"))==false){
					mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("BSprint",new byte[]{ 4 }));
					connectedServerResponse=4;
				}
				else svFlyingBoost=svRunInAllDirs=true;
			}
			else if (connectedServerResponse>0){
				--connectedServerResponse;
				GuiNewChat chat=mc.ingameGUI.getChatGUI();
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
					if (chatLine.getChatLineString().startsWith("\u00a7b\u00a7r\u00a74\u00a7")){
						String[] set=chatLine.getChatLineString().substring(7).split("\u00a7");
						for(int a=0; a<set.length; a++){
							if (!set[a].equals("1"))continue;
							if (a==0)svFlyingBoost=true;
							else if (a==1)svRunInAllDirs=true;
							
						}
						connectedServerResponse=0;
						chat.deleteChatLine(chatLine.getChatLineID());
						break;
					}
				}
			}
		}
	}
	
	// GUI
	
	private GuiScreen parentScreen;
	private int buttonId = -1;
	private GuiButton btnDoubleTap,btnFlyBoost,btnAllDirs;
	protected KeyBinding[] sprintBindings=new KeyBinding[]{
		keyBindSprint, keyBindSprintToggle, keyBindSneakToggle, keyBindSprintMenu
	};
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen=parentScreen;
	}
	
	public void initGui(){
		GuiScreen.fromBs = false;
        //StringTranslate var1 = StringTranslate.getInstance();
        int left=getLeftColumnX(), ypos=0;

        for(int a=0; a<sprintBindings.length; ++a){
			ypos=height/6+24*(a>>1);
			buttonList.add(new GuiSmallButton(a,left+a%2*160,ypos,70,20,getKeyCodeString(a)));
		}
        
        ypos+=48;
        btnDoubleTap=new GuiButton(199,left,ypos,70,20,""); buttonList.add(btnDoubleTap);
        btnAllDirs=new GuiButton(198,left+160,ypos,70,20,""); buttonList.add(btnAllDirs);
        if (!GuiScreen.canRunInAllDirs(mc))btnAllDirs.enabled=false;
        ypos+=24;
        btnFlyBoost=new GuiButton(197,left,ypos,70,20,""); buttonList.add(btnFlyBoost);
        if (!GuiScreen.canBoostFlying(mc))btnFlyBoost.enabled=false;
        
        buttonList.add(new GuiButton(200,width/2-100,height/6+168,parentScreen==null?98:200,20,I18n.func_135053_a("gui.done")));
        if (parentScreen==null)buttonList.add(new GuiButton(190,width/2+2,height/6+168,98,20,I18n.func_135053_a("options.controls")));
        updateButtons();
    }
	
	private void updateButtons(){
		btnDoubleTap.displayString=allowDoubleTap?"Enabled":"Disabled";
		btnFlyBoost.displayString=GuiScreen.canBoostFlying(mc)?(flyingBoost==0?"Disabled":(flyingBoost+1)+"x"):"Unavailable";
		btnAllDirs.displayString=GuiScreen.canRunInAllDirs(mc)?(allowAllDirs?"Enabled":"Disabled"):"Unavailable";
	}
	
	protected void actionPerformed(GuiButton btn){
		for(int var2=0; var2<sprintBindings.length; ++var2){
			((GuiButton)buttonList.get(var2)).displayString=getKeyCodeString(var2);
		}

		switch(btn.id){
			case 190:
				GuiScreen.fromBs=true;
				mc.displayGuiScreen(new GuiControls(this,mc.gameSettings));
				break;
				
			case 197:
				if (GuiScreen.canBoostFlying(mc)&&++flyingBoost==8)flyingBoost=0;
				break;
				
			case 198:
				if (GuiScreen.canRunInAllDirs(mc))allowAllDirs=!allowAllDirs;
				break;
				
			case 199:
				allowDoubleTap=!allowDoubleTap;
				break;
				
			case 200:
				if (parentScreen==null){
					mc.displayGuiScreen((GuiScreen)null);
	                mc.setIngameFocus();
	                mc.sndManager.resumeAllSounds();
				}
				else{
					mc.displayGuiScreen(parentScreen);
					parentScreen.prevWidth=0;
				}
				break;
				
			default:
				buttonId=btn.id;
				btn.displayString="> "+mc.gameSettings.getOptionDisplayString(btn.id)+" <";
		}
		saveSprint(mc);
		updateButtons();
	}

	protected void mouseClicked(int par1, int par2, int par3){
		if (!handleInput(-100+par3))super.mouseClicked(par1,par2,par3);
	}

	protected void keyTyped(char par1, int par2){
		if (!handleInput(par2))super.keyTyped(par1,par2);
	}
	
	private boolean handleInput(int par1){
		if (buttonId>=0&&buttonId<180){
			sprintBindings[buttonId].keyCode=par1;
			((GuiButton)buttonList.get(buttonId)).displayString=getKeyCodeString(buttonId);
			buttonId=-1;
			KeyBinding.resetKeyBindingArrayAndHash();
			return true;
		}
		return false;
	}
	
	public void drawScreen(int par1, int par2, float par3){
        drawDefaultBackground();
		drawCenteredString(fontRenderer,"Better Sprinting",width/2,20,16777215);

		int left=getLeftColumnX(),a=0;
		while(a<sprintBindings.length){
			boolean alreadyUsed=false;
			int b=0;

			while(true){
				if (b<sprintBindings.length){
					if (b==a||sprintBindings[a].keyCode!=sprintBindings[b].keyCode){
						++b;
						continue;
					}
					alreadyUsed=true;
				}
				
				for(int i=0; i<mc.gameSettings.keyBindings.length; i++){
					if (sprintBindings[a].keyCode==mc.gameSettings.keyBindings[i].keyCode){
						alreadyUsed=true;
						break;
					}
				}

				if (buttonId==a)((GuiButton)buttonList.get(a)).displayString="\u00a7f> \u00a7e??? \u00a7f<";
				else if (alreadyUsed)((GuiButton)buttonList.get(a)).displayString="\u00a7c"+getKeyCodeString(a);
				else ((GuiButton)buttonList.get(a)).displayString=getKeyCodeString(a);

				drawString(fontRenderer,sprintBindings[a].keyDescription,left+a%2*160+70+6,height/6+24*(a>>1)+7,-1);
				++a;
				break;
			}
		}

		drawButtonTitle("Double tapping",btnDoubleTap);
		drawButtonTitle("Run in all directions",btnAllDirs);
		drawButtonTitle("Flying boost",btnFlyBoost);
		
		if (isPlayerClassEdited)drawCenteredString(fontRenderer,"\u00a7cDetected conflicted class, vital mod functions may not work!",width/2,30,-1);

		super.drawScreen(par1,par2,par3);
    }
	
	private int getLeftColumnX(){
        return width/2-155;
    }
	
	private String getKeyCodeString(int i){
		return GameSettings.getKeyDisplayString(sprintBindings[i].keyCode);
	}
	
	private void drawButtonTitle(String title, GuiButton btn){
		drawString(fontRenderer,title,btn.xPosition+70+6,btn.yPosition+7,-1);
	}
}

// CHANGED (NEW) CLASS