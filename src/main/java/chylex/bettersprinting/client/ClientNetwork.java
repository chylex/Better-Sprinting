package chylex.bettersprinting.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;

@OnlyIn(Dist.CLIENT)
public class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(int protocol){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	@Override
	public void onPacket(ByteBuf data, EntityPlayer player){
		byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyingBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = true;
			ClientModManager.showDisableWarningWhenPossible = true;
		}
		else if (type == 2 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = false;
			ClientModManager.showDisableWarningWhenPossible = true;
		}
	}
}
