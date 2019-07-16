package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class LivingUpdate{
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	private static boolean isModDisabled;
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12.2
	public static void injectMovementInputUpdate(EntityPlayerSP $this){
		hasTriggered = true;
		isModDisabled = ClientModManager.isModDisabled();
		
		// this.movementInput.updatePlayerMoveState(); <<< REPLACE
		
		if (isModDisabled){
			if (currentHandler != null){
				currentHandler.resetState();
				currentHandler = null;
			}
			
			$this.movementInput.updatePlayerMoveState();
			return;
		}
		
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
		}
		
		currentHandler.updateMovementInput();
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12.2
	public static boolean injectSprinting(){
		if (isModDisabled){
			return false;
		}
		
		/*
		}
		<<< INSERTED HERE
		boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
		*/
		
		currentHandler.updateSprinting();
		return true;
		
		/*
		}
		<<< SKIPPED TO HERE
		if (this.capabilities.allowFlying)
		*/
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12.2
	public static boolean injectAfterSuperCall(){
		if (isModDisabled){
			return false;
		}
		
		/*
		super.onLivingUpdate();
		<<< INSERTED HERE
		if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode())
		*/
		
		currentHandler.updateFlight();
		return true;
		
		/*
			this.sendPlayerAbilities();
		}
		<<< SKIPPED TO HERE
		*/
	}
	
	private LivingUpdate(){}
}
