package chylex.bettersprinting.client.compatibility;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class OldNotificationPacket{
	public static void sendServerNotification(NetHandlerPlayClient netHandler){
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		buffer.writeByte(5);
		netHandler.addToSendQueue(new C17PacketCustomPayload("BSprint", buffer));
	}
}
