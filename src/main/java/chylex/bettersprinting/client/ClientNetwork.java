package chylex.bettersprinting.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;

public class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(int protocol){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){
		byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyingBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1){
			ClientModManager.svDisableMod = true;
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"[Better Sprinting]"+EnumChatFormatting.RESET+" The server has requested to disable the mod, the sprinting mechanics are switched to vanilla until you disconnect."));
		}
	}
}
