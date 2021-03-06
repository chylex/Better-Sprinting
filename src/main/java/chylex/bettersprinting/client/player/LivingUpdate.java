package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public final class LivingUpdate{
	private static PlayerLogicHandler currentHandler;
	private static boolean isModDisabled;
	
	public static void cleanup(){
		currentHandler = null;
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.16.2
	public static void injectMovementInputUpdate(final ClientPlayerEntity player, final boolean slowMovement){
		IntegrityCheck.isValidated = true;
		isModDisabled = ClientModManager.isModDisabled();
		
		// this.movementInput.tickMovement(this.func_228354_I_()); <<< REPLACE
		
		if (isModDisabled){
			if (currentHandler != null){
				currentHandler.resetState();
				currentHandler = null;
			}
			
			player.movementInput.tickMovement(slowMovement);
			return;
		}
		
		if (currentHandler == null || currentHandler.getPlayer() != player){
			currentHandler = new PlayerLogicHandler(player);
		}
		
		currentHandler.updateMovementInput(slowMovement);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.16.2
	public static boolean injectSprinting(){
		if (isModDisabled){
			return false;
		}
		
		/*
		}
		<<< INSERTED HERE
		if (flag1) {
         this.sprintToggleTimer = 0;
        }
        
        boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		*/
		
		currentHandler.updateSprinting();
		return true;
		
		/*
		}
		<<< SKIPPED TO HERE
		if (this.abilities.allowFlying) {
		*/
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.16.2
	public static boolean injectFlightCancelTest(){
		if (isModDisabled){
			return false;
		}
		
		/*
		if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode() >>> INSERTED HERE <<< ) {
		*/
		
		return currentHandler.shouldPreventCancelingFlight();
		
		/*
			this.sendPlayerAbilities();
		}
		<<< SKIPPED TO HERE
		*/
	}
	
	private LivingUpdate(){}
}
