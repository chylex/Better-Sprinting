package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;

@SideOnly(Side.CLIENT)
public final class LivingUpdate{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.8.9
	public static void injectOnLivingUpdate(EntityPlayerSP $this){
		/*
		if (this.timeUntilPortal > 0){
			--this.timeUntilPortal;
		}
		
		boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
		<<< INSERTED HERE
		float f = 0.8F;
		boolean flag2 = this.movementInput.moveForward >= f;
		*/
		
		// CUSTOM
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
			hasTriggered = true;
		}
		
		if (mc.playerController.isInCreativeMode() && $this.capabilities.isFlying && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround){
			$this.onGround = false;
		}
		
		boolean wasJumping = $this.movementInput.jump;
		currentHandler.updateMovementInput();
		
		// VANILLA
		if ($this.isUsingItem() && !$this.isRiding()){
			$this.movementInput.moveStrafe *= 0.2F;
			$this.movementInput.moveForward *= 0.2F;
			$this.sprintToggleTimer = 0;
		}
		
		$this.pushOutOfBlocks($this.posX - $this.width * 0.35D, $this.getEntityBoundingBox().minY + 0.5D, $this.posZ + $this.width * 0.35D);
		$this.pushOutOfBlocks($this.posX - $this.width * 0.35D, $this.getEntityBoundingBox().minY + 0.5D, $this.posZ - $this.width * 0.35D);
		$this.pushOutOfBlocks($this.posX + $this.width * 0.35D, $this.getEntityBoundingBox().minY + 0.5D, $this.posZ - $this.width * 0.35D);
		$this.pushOutOfBlocks($this.posX + $this.width * 0.35D, $this.getEntityBoundingBox().minY + 0.5D, $this.posZ + $this.width * 0.35D);
		
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
			else if (!wasJumping && $this.movementInput.jump){
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
		
		/*
		}
		<<< SKIPPED TO HERE
		if (this.capabilities.isFlying && this.isCurrentViewEntity())
		*/
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.8.9
	public static void injectOnLivingUpdateEnd(EntityPlayerSP $this){
		/*
		else{
			this.horseJumpPower = 0.0F;
		}
		
		super.onLivingUpdate();
		<<< INSERTED HERE
		if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode())
		*/
		
		if ($this.onGround && $this.capabilities.isFlying && !mc.playerController.isSpectatorMode()){
			boolean shouldFlyOnGround = mc.playerController.isInCreativeMode() && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround;
			
			if (!shouldFlyOnGround){
				$this.capabilities.isFlying = false;
				$this.sendPlayerAbilities();
			}
		}
	}
	
	private LivingUpdate(){}
}
