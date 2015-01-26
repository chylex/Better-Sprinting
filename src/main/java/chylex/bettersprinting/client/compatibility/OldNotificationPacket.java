package chylex.bettersprinting.client.compatibility;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class OldNotificationPacket{
	public static void sendServerNotification(){
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		buffer.writeByte(5);
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("BSprint",buffer));
	}
}
