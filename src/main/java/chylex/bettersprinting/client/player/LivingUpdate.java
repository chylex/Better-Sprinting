package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public static void injectOnLivingUpdate(EntityPlayerSP $this){
		/*
		if (this.timeUntilPortal > 0){
			--this.timeUntilPortal;
		}
		
		boolean flag = this.movementInput.jump;
		boolean flag1 = this.movementInput.sneak;    <<< INSERTED HERE
		float f = 0.8F;
		boolean flag2 = this.movementInput.moveForward >= 0.8F;
		 */
		
		// CUSTOM
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
			hasTriggered = true;
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
		
		AxisAlignedBB playerBoundingBox = $this.getBoundingBox();
		PlayerSPPushOutOfBlocksEvent event = new PlayerSPPushOutOfBlocksEvent($this, playerBoundingBox);
		
		if (!MinecraftForge.EVENT_BUS.post(event)){
			playerBoundingBox = event.getEntityBoundingBox();
			$this.pushOutOfBlocks($this.posX - $this.width * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ + $this.width * 0.35D);
			$this.pushOutOfBlocks($this.posX - $this.width * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ - $this.width * 0.35D);
			$this.pushOutOfBlocks($this.posX + $this.width * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ - $this.width * 0.35D);
			$this.pushOutOfBlocks($this.posX + $this.width * 0.35D, playerBoundingBox.minY + 0.5D, $this.posZ + $this.width * 0.35D);
		}
		
		// CUSTOM
		currentHandler.updateLiving();
		
		// VANILLA
		if ($this.abilities.allowFlying){
			if (mc.playerController.isSpectatorMode()){
				if (!$this.abilities.isFlying){
					$this.abilities.isFlying = true;
					$this.sendPlayerAbilities();
				}
			}
			else if (!wasJumping && $this.movementInput.jump && !hasAutoJumped){
				if ($this.flyToggleTimer == 0){
					$this.flyToggleTimer = 7;
				}
				else{
					$this.abilities.isFlying = !$this.abilities.isFlying;
					$this.sendPlayerAbilities();
					$this.flyToggleTimer = 0;
				}
			}
		}
		
		/*
		}
		    <<< SKIPPED TO HERE
		if (this.movementInput.jump && !flag && !this.onGround && this.motionY < 0.0D && !this.isElytraFlying() && !this.abilities.isFlying)
		{
			ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		*/
	}
	
	private LivingUpdate(){}
}
