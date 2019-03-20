package chylex.bettersprinting.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.gui.GuiSprint;


public class PlayerBase extends ClientPlayerBase{
	private static boolean shouldRestoreSneakToggle = false;
	private static String connectedServer = "";
	private static byte connectedServerResponse = 0;
	private static byte behaviorCheckTimer = 10;
	
	private Minecraft mc;
	private CustomMovementInput customMovementInput;
	
	public PlayerBase(ClientPlayerAPI api){
		super(api);
		mc=Minecraft.getMinecraft();
		customMovementInput=new CustomMovementInput();
	}
	
	@Override
	public void onLivingUpdate(){
		if (player.sprintingTicksLeft>0){
			--player.sprintingTicksLeft;

			if (player.sprintingTicksLeft==0){
				player.setSprinting(false);
			}
		}

		
		if (playerAPI.getSprintToggleTimerField()>0){
			playerAPI.setSprintToggleTimerField(playerAPI.getSprintToggleTimerField()-1);
		}

		if (mc.playerController.enableEverythingIsScrewedUpMode()){
			player.posX=player.posZ=0.5D;
			player.posX=0.0D;
			player.posZ=0.0D;
			player.rotationYaw=player.ticksExisted/12.0F;
			player.rotationPitch=10.0F;
			player.posY=68.5D;
		}
		else{
			player.prevTimeInPortal=player.timeInPortal;

			if (playerAPI.getInPortalField()){
				if (mc.currentScreen!=null){
					mc.displayGuiScreen((GuiScreen)null);
				}

				if (player.timeInPortal==0.0F){
					mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("portal.trigger"),player.getRNG().nextFloat()*0.4F+0.8F));
				}

				player.timeInPortal+=0.0125F;

				if (player.timeInPortal>=1.0F){
					player.timeInPortal=1.0F;
				}

				playerAPI.setInPortalField(false);
			}
			else if (player.isPotionActive(Potion.confusion)&&player.getActivePotionEffect(Potion.confusion).getDuration()>60){
				player.timeInPortal+=0.006666667F;

				if (player.timeInPortal>1.0F){
					player.timeInPortal=1.0F;
				}
			}
			else{
				if (player.timeInPortal>0.0F){
					player.timeInPortal-=0.05F;
				}

				if (player.timeInPortal<0.0F){
					player.timeInPortal=0.0F;
				}
			}

			if (player.timeUntilPortal>0){
				--player.timeUntilPortal;
			}

			boolean isJumping=player.movementInput.jump;
			float minSpeed=0.8F;
			boolean isMovingForward=player.movementInput.moveForward>=minSpeed;
			customMovementInput.update(mc,(MovementInputFromOptions)player.movementInput); // CHANGED LINE

			if (player.isUsingItem()&&!player.isRiding()){
				player.movementInput.moveStrafe*=0.2F;
				player.movementInput.moveForward*=0.2F;
				playerAPI.setSprintToggleTimerField(0);
			}

			if (player.movementInput.sneak&&player.ySize<0.2F){
				player.ySize=0.2F;
			}
			
			playerAPI.localPushOutOfBlocks(player.posX-player.width*0.35D,player.boundingBox.minY+0.5D,player.posZ+player.width*0.35D);
			playerAPI.localPushOutOfBlocks(player.posX-player.width*0.35D,player.boundingBox.minY+0.5D,player.posZ-player.width*0.35D);
			playerAPI.localPushOutOfBlocks(player.posX+player.width*0.35D,player.boundingBox.minY+0.5D,player.posZ-player.width*0.35D);
			playerAPI.localPushOutOfBlocks(player.posX+player.width*0.35D,player.boundingBox.minY+0.5D,player.posZ+player.width*0.35D);
			boolean enoughHunger=player.getFoodStats().getFoodLevel()>6.0F||player.capabilities.allowFlying;
			
			// CHANGE
			if (ClientModManager.disableModFunctionality){
				if (player.onGround && !isMovingForward && player.movementInput.moveForward>=minSpeed && !player.isSprinting()&&enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
					if (playerAPI.getSprintToggleTimerField()<=0 && !ClientModManager.keyBindSprint.getIsKeyPressed()){
						playerAPI.setSprintToggleTimerField(7);
					}
					else player.setSprinting(true);
				}

				if (!player.isSprinting() && player.movementInput.moveForward>=minSpeed && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness) && ClientModManager.keyBindSprint.getIsKeyPressed()){
					player.setSprinting(true);
				}
			}
			else{
				updateBehavior();
				boolean lastheld=ClientModManager.held;
				boolean state=customMovementInput.sprint;
				boolean doubletap=ClientModManager.allowDoubleTap||ClientModManager.disableModFunctionality;
	
				if (!player.capabilities.isFlying&&((MovementInputFromOptions)player.movementInput).sneak)state=false;
				if (((doubletap && !player.isSprinting()) || !doubletap) && player.onGround && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
					player.setSprinting(state);
				}
				ClientModManager.held=state;
	
				if (doubletap && !ClientModManager.held && ClientModManager.stoptime==0 && player.onGround && !isMovingForward && player.movementInput.moveForward>=minSpeed && !player.isSprinting() && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
					if (playerAPI.getSprintToggleTimerField()==0){
						playerAPI.setSprintToggleTimerField(7);
					}
					else{
						player.setSprinting(true);
						playerAPI.setSprintToggleTimerField(0);
					}
				}
				if (doubletap){
					if (lastheld&&!ClientModManager.held)ClientModManager.stoptime=1;
					if (ClientModManager.stoptime>0){
						ClientModManager.stoptime--;
						player.setSprinting(false);
					}
				}
	
				if (ClientModManager.flyingBoost>0){
					if (state&&player.capabilities.isFlying&&ClientModManager.canBoostFlying(mc)){
						player.capabilities.setFlySpeed(0.05F*(1+ClientModManager.flyingBoost));
						if (player.movementInput.sneak){
							player.motionY-=0.15D*ClientModManager.flyingBoost;
						}
	
						if (player.movementInput.jump){
							player.motionY+=0.15D*ClientModManager.flyingBoost;
						}
					}
					else player.capabilities.setFlySpeed(0.05F);
				}
				else if (player.capabilities.getFlySpeed()>0.05F)player.capabilities.setFlySpeed(0.05F);
			}

			if (ClientModManager.keyBindSprintMenu.getIsKeyPressed())mc.displayGuiScreen(new GuiSprint(null));
			if (player.isSprinting()&&player.isSneaking()&&!player.capabilities.isFlying)player.setSprinting(false);
			// END

			if (player.isSprinting() && (player.movementInput.moveForward<minSpeed || player.isCollidedHorizontally || !enoughHunger)){
				if ((ClientModManager.canRunInAllDirs(mc)&&ClientModManager.allowAllDirs)==false||(player.movementInput.moveForward==0F&&player.movementInput.moveStrafe==0F))player.setSprinting(false); // CHANGED																																					// LINE
			}

			if (player.capabilities.allowFlying&&!isJumping&&player.movementInput.jump){
				if (playerAPI.getFlyToggleTimerField()==0){
					playerAPI.setFlyToggleTimerField(7);
				}
				else{
					player.capabilities.isFlying=!player.capabilities.isFlying;
					player.sendPlayerAbilities();
					playerAPI.setFlyToggleTimerField(0);
				}
			}

			if (player.capabilities.isFlying){
				if (player.movementInput.sneak){
					player.motionY-=0.15D;
				}

				if (player.movementInput.jump){
					player.motionY+=0.15D;
				}
			}

			if (player.isRidingHorse()){
				if (playerAPI.getHorseJumpPowerCounterField()<0){
					playerAPI.setHorseJumpPowerCounterField(playerAPI.getHorseJumpPowerCounterField()+1);

					if (playerAPI.getHorseJumpPowerCounterField()==0){
						playerAPI.setHorseJumpPowerField(0F);
					}
				}

				if (isJumping&&!player.movementInput.jump){
					playerAPI.setHorseJumpPowerCounterField(-10);
					((EntityClientPlayerMP)player).sendQueue.addToSendQueue(new C0BPacketEntityAction(player,6,(int)(player.getHorseJumpPower()*100F)));
				}
				else if (!isJumping&&player.movementInput.jump){
					playerAPI.setHorseJumpPowerCounterField(0);
					playerAPI.setHorseJumpPowerField(0F);
				}
				else if (isJumping){
					playerAPI.setHorseJumpPowerCounterField(playerAPI.getHorseJumpPowerCounterField()+1);

					if (playerAPI.getHorseJumpPowerCounterField()<10){
						playerAPI.setHorseJumpPowerField(playerAPI.getHorseJumpPowerCounterField()*0.1F);
					}
					else{
						playerAPI.setHorseJumpPowerField(0.8F+2.0F/(playerAPI.getHorseJumpPowerCounterField()-9)*0.1F);
					}
				}
			}
			else{
				playerAPI.setHorseJumpPowerField(0F);
			}

			playerAPI.superOnLivingUpdate();

			if (player.onGround&&player.capabilities.isFlying){
				player.capabilities.isFlying=false;
				player.sendPlayerAbilities();
			}
		}
	}
	
	private void updateBehavior(){
		if (mc.currentScreen!=null&&player!=null&&player.isSneaking()){
			if (customMovementInput.sneakToggle&&!(mc.currentScreen instanceof GuiGameOver)){
				if (!ClientModManager.showedToggleSneakWarning){
					player.addChatMessage(new ChatComponentText("First-time warning: You can open inventories and menus while sneaking, however you will not be sneaking for the time it is open. Once you close the menu, sneaking will be restored."));
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					ClientModManager.showedToggleSneakWarning=true;
					ClientModManager.saveSprint(mc);
				}
				else{
					shouldRestoreSneakToggle=true;
					customMovementInput.sneakToggle=false;
				}
			}
		}
		
		if (shouldRestoreSneakToggle&&mc.currentScreen==null){
			customMovementInput.sneakToggle=true;
			shouldRestoreSneakToggle=false;
		}

		if (behaviorCheckTimer>0){
			--behaviorCheckTimer;
			return;
		}
		behaviorCheckTimer = 10;
		
		if (mc.thePlayer==null){
			connectedServer="";
			ClientModManager.svFlyingBoost=ClientModManager.svRunInAllDirs=false;
		}
		else if (!mc.isIntegratedServerRunning()&&mc.func_147104_D()!=null&&!ClientModManager.disableModFunctionality){
			String serverIP=mc.func_147104_D().serverIP;
			
			if (!connectedServer.equals(serverIP)){
				ClientModManager.svFlyingBoost=ClientModManager.svRunInAllDirs=false;
				connectedServer=new String(serverIP);
				
				if ((connectedServer.startsWith("127.0.0.1")||connectedServer.equals("localhost"))==false){
					mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("BSprint",new byte[]{ 4 }));
					connectedServerResponse=4;
				}
				else ClientModManager.svFlyingBoost=ClientModManager.svRunInAllDirs=true;
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
}
