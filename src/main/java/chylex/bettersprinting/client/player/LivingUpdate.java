package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public final class LivingUpdate{
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.2
	public static void injectOnLivingUpdate(ClientPlayerEntity $this){
		/*
		this.func_213839_ed();
		boolean flag = this.movementInput.jump;
		boolean flag1 = this.movementInput.sneak;
		<<< INSERTED HERE
		boolean flag2 = this.func_223110_ee();
		*/
		
		// CUSTOM
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
			hasTriggered = true;
		}
		
		if (mc.field_71442_b.isInCreativeMode() && $this.playerAbilities.isFlying && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get()){
			$this.onGround = false;
		}
		
		boolean wasJumping = $this.movementInput.jump;
		currentHandler.updateMovementInput();
		
		// VANILLA
		if ($this.isHandActive() && !$this.isPassenger()){
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
		
		if (!$this.noClip){
			AxisAlignedBB playerBoundingBox = $this.getBoundingBox();
			PlayerSPPushOutOfBlocksEvent event = new PlayerSPPushOutOfBlocksEvent($this, playerBoundingBox);
			
			if (!MinecraftForge.EVENT_BUS.post(event)){
				playerBoundingBox = event.getEntityBoundingBox();
				$this.func_213282_i($this.posX - $this.getWidth() * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ + $this.getWidth() * 0.35D);
				$this.func_213282_i($this.posX - $this.getWidth() * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ - $this.getWidth() * 0.35D);
				$this.func_213282_i($this.posX + $this.getWidth() * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ - $this.getWidth() * 0.35D);
				$this.func_213282_i($this.posX + $this.getWidth() * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ + $this.getWidth() * 0.35D);
			}
		}
		
		// CUSTOM
		currentHandler.updateLiving();
		
		// VANILLA
		if ($this.playerAbilities.allowFlying){
			if (mc.field_71442_b.isSpectatorMode()){
				if (!$this.playerAbilities.isFlying){
					$this.playerAbilities.isFlying = true;
					$this.sendPlayerAbilities();
				}
			}
			else if (!wasJumping && $this.movementInput.jump && !hasAutoJumped){
				if ($this.flyToggleTimer == 0){
					$this.flyToggleTimer = 7;
				}
				else if (!$this.isSwimming()){
					$this.playerAbilities.isFlying = !$this.playerAbilities.isFlying;
					$this.sendPlayerAbilities();
					$this.flyToggleTimer = 0;
				}
			}
		}
		
		/*
		}
		<<< SKIPPED TO HERE
		if (this.movementInput.jump && !flag && !this.onGround && this.motionY < 0.0D && !this.isElytraFlying() && !this.playerAbilities.isFlying){
			ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		*/
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.2
	public static void injectOnLivingUpdateEnd(ClientPlayerEntity $this){
		/*
		else{
			this.horseJumpPower = 0.0F;
		}
		
		super.livingTick();
		<<< INSERTED HERE
		
		if (this.onGround && this.playerAbilities.isFlying && !this.mc.field_71442_b.isSpectatorMode()){
		*/
		
		if ($this.onGround && $this.playerAbilities.isFlying && !mc.field_71442_b.isSpectatorMode()){
			boolean shouldFlyOnGround = mc.field_71442_b.isInCreativeMode() && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get();
			
			if (!shouldFlyOnGround){
				$this.playerAbilities.isFlying = false;
				$this.sendPlayerAbilities();
			}
		}
	}
	
	private LivingUpdate(){}
}
