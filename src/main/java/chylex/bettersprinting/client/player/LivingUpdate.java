package chylex.bettersprinting.client.player;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class LivingUpdate{
	private static final Minecraft mc;
	private static final MethodHandle mPushOutOfBlocks;
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	
	static{
		mc = Minecraft.getMinecraft();
		
		try{
			mPushOutOfBlocks = MethodHandles.lookup().unreflect(EntityPlayerSP.class.getMethod("_bsm_pushOutOfBlocks", double.class, double.class, double.class));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12.2
	public static void callPreSuper(EntityPlayerSP $this){
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
			hasTriggered = true;
		}
		
		// VANILLA
		++$this.sprintingTicksLeft;
		
		if ($this.sprintToggleTimer > 0){
			--$this.sprintToggleTimer;
		}
		
		$this.prevTimeInPortal = $this.timeInPortal;
		
		if ($this.inPortal){
			if (mc.currentScreen != null && !mc.currentScreen.doesGuiPauseGame()){
				if (mc.currentScreen instanceof GuiContainer){
					$this.closeScreen();
				}
				
				mc.displayGuiScreen(null);
			}
			
			if ($this.timeInPortal == 0F){
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRIGGER, $this.getRNG().nextFloat()*0.4F+0.8F));
			}
			
			$this.timeInPortal += 0.0125F;
			
			if ($this.timeInPortal >= 1F){
				$this.timeInPortal = 1F;
			}
			
			$this.inPortal = false;
		}
		else if ($this.isPotionActive(MobEffects.NAUSEA) && $this.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60){
			$this.timeInPortal += 0.006666667F;
			
			if ($this.timeInPortal > 1F){
				$this.timeInPortal = 1F;
			}
		}
		else{
			if ($this.timeInPortal > 0F){
				$this.timeInPortal -= 0.05F;
			}
			
			if ($this.timeInPortal < 0F){
				$this.timeInPortal = 0F;
			}
		}
		
		if ($this.timeUntilPortal > 0){
			--$this.timeUntilPortal;
		}
		
		boolean wasJumping = $this.movementInput.jump;
		currentHandler.updateMovementInput();
		
		if ($this.isHandActive() && !$this.isRiding()){
			$this.movementInput.moveStrafe *= 0.2F;
			$this.movementInput.moveForward *= 0.2F;
			$this.sprintToggleTimer = 0;
		}
		
		boolean hasAutoJumped = false;
		
		if ($this.autoJumpTime > 0){
			--$this.autoJumpTime;
			hasAutoJumped = true;
			$this.movementInput.jump = true;
		}
		
		AxisAlignedBB playerBoundingBox = $this.getEntityBoundingBox();
		PlayerSPPushOutOfBlocksEvent event = new PlayerSPPushOutOfBlocksEvent($this, playerBoundingBox);
		
        if (!MinecraftForge.EVENT_BUS.post(event)){
			try{
				mPushOutOfBlocks.invokeExact($this, $this.posX-$this.width*0.35D, playerBoundingBox.minY+0.5D, $this.posZ+$this.width*0.35D);
				mPushOutOfBlocks.invokeExact($this, $this.posX-$this.width*0.35D, playerBoundingBox.minY+0.5D, $this.posZ-$this.width*0.35D);
				mPushOutOfBlocks.invokeExact($this, $this.posX+$this.width*0.35D, playerBoundingBox.minY+0.5D, $this.posZ-$this.width*0.35D);
				mPushOutOfBlocks.invokeExact($this, $this.posX+$this.width*0.35D, playerBoundingBox.minY+0.5D, $this.posZ+$this.width*0.35D);
			}catch(Throwable e){
				throw new RuntimeException(e);
			}
        }
		
		// CUSTOM
		currentHandler.updateLiving();
		
		// VANILLA
		if ($this.capabilities.allowFlying){
			if (mc.playerController.isSpectatorMode()){
				if (!$this.capabilities.isFlying){
					$this.capabilities.isFlying = true;
					$this.sendPlayerAbilities();
				}
			}
			else if (!wasJumping && $this.movementInput.jump && !hasAutoJumped){
				if ($this.flyToggleTimer == 0){
					$this.flyToggleTimer = 7;
				}
				else{
					$this.capabilities.isFlying = !$this.capabilities.isFlying;
					$this.sendPlayerAbilities();
					$this.flyToggleTimer = 0;
				}
			}
		}
		
		if ($this.movementInput.jump && !wasJumping && !$this.onGround && $this.motionY < 0D && !$this.isElytraFlying() && !$this.capabilities.isFlying){
			ItemStack chestIS = $this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			
			if (chestIS.getItem() == Items.ELYTRA && ItemElytra.isUsable(chestIS)){
				$this.connection.sendPacket(new CPacketEntityAction($this, CPacketEntityAction.Action.START_FALL_FLYING));
			}
		}
		
		$this.wasFallFlying = $this.isElytraFlying();

		if ($this.capabilities.isFlying && callIsCurrentViewEntity($this)){
			if ($this.movementInput.sneak){
				$this.movementInput.moveStrafe = $this.movementInput.moveStrafe/0.3F;
				$this.movementInput.moveForward = $this.movementInput.moveForward/0.3F;
				$this.motionY -= 0.15D; // ignore capabilities.getFlySpeed()
			}

			if ($this.movementInput.jump){
				$this.motionY += 0.15D; // ignore capabilities.getFlySpeed()
			}
		}

		if ($this.isRidingHorse()){
			IJumpingMount mount = (IJumpingMount)$this.getRidingEntity();
			
			if ($this.horseJumpPowerCounter < 0){
				++$this.horseJumpPowerCounter;
				
				if ($this.horseJumpPowerCounter == 0){
					$this.horseJumpPower = 0F;
				}
			}

			if (wasJumping && !$this.movementInput.jump){
				$this.horseJumpPowerCounter = -10;
				mount.setJumpPower(MathHelper.floor($this.getHorseJumpPower()*100F));
				callSendHorseJump($this);
			}
			else if (!wasJumping && $this.movementInput.jump){
				$this.horseJumpPowerCounter = 0;
				$this.horseJumpPower = 0F;
			}
			else if (wasJumping){
				++$this.horseJumpPowerCounter;
				
				if ($this.horseJumpPowerCounter < 10){
					$this.horseJumpPower = $this.horseJumpPowerCounter*0.1F;
				}
				else{
					$this.horseJumpPower = 0.8F+2.0F/($this.horseJumpPowerCounter-9)*0.1F;
				}
			}
		}
		else{
			$this.horseJumpPower = 0F;
		}
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12.2
	public static void callPostSuper(EntityPlayerSP $this){
		if ($this.onGround && $this.capabilities.isFlying && !mc.playerController.isSpectatorMode()){
			$this.capabilities.isFlying = false;
			$this.sendPlayerAbilities();
		}
	}
	
	// UPDATE | EntityPlayerSP.isCurrentViewEntity | 1.12.2
	private static boolean callIsCurrentViewEntity(EntityPlayerSP $this){
		return mc.getRenderViewEntity() == $this;
	}
	
	// UPDATE | EntityPlayerSP.sendHorseJump | 1.12.2
	private static void callSendHorseJump(EntityPlayerSP $this){
		$this.connection.sendPacket(new CPacketEntityAction($this, CPacketEntityAction.Action.START_RIDING_JUMP, (int)($this.getHorseJumpPower()*100F)));
	}
	
	private LivingUpdate(){}
}
