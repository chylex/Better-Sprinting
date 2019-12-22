package chylex.bettersprinting.client;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public final class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(int protocol){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	private static PacketBuffer writeLanSettings(){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeBoolean(false).writeBoolean(true);
		return buffer;
	}
	
	@Override
	public void onPacket(LogicalSide side, ByteBuf data, PlayerEntity player){
		if (side == LogicalSide.SERVER){
			PacketPipeline.sendToPlayer(writeLanSettings(), player);
			return;
		}
		
		byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = true;
			ClientEventHandler.showDisableWarningWhenPossible = true;
		}
		else if (type == 2 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = false;
			ClientEventHandler.showDisableWarningWhenPossible = true;
		}
	}
}
