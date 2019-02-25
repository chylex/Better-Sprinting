package chylex.bettersprinting.system;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent.ClientCustomPayloadEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkEvent.ServerCustomPayloadEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class PacketPipeline{
	private static final ResourceLocation channelName = new ResourceLocation("bsm", "settings");
	private static final String protocolId = "1";
	
	private static boolean registered = false;
	
	public static void initialize(INetworkHandler handler){
		if (registered){
			throw new RuntimeException("Packet pipeline has already been registered!");
		}
		
		NetworkRegistry.newEventChannel(channelName, () -> protocolId, protocolServer -> true, protocolClient -> true).registerObject(new PacketPipeline(handler));
		registered = true;
	}
	
	private final INetworkHandler handler;
	
	private PacketPipeline(INetworkHandler handler){
		this.handler = handler;
	}
	
	@SubscribeEvent
	public void onServerToClientPacket(ServerCustomPayloadEvent e){
		handlePacket(getClientPlayer(), e.getPayload().copy(), e.getSource().get());
	}
	
	@SubscribeEvent
	public void onClientToServerPacket(ClientCustomPayloadEvent e){
		Context ctx = e.getSource().get();
		handlePacket(ctx.getSender(), e.getPayload().copy(), ctx);
	}
	
	private void handlePacket(EntityPlayer player, ByteBuf payload, Context ctx){
		ctx.enqueueWork(() -> handler.onPacket(payload, player));
	}
	
	@OnlyIn(Dist.CLIENT)
	private EntityPlayer getClientPlayer(){
		return Minecraft.getInstance().player;
	}
	
	public static PacketBuffer buf(){
		return new PacketBuffer(Unpooled.buffer());
	}
	
	public static void sendToPlayer(PacketBuffer buffer, EntityPlayer player){
		PacketDistributor.PLAYER.with(() -> (EntityPlayerMP)player).send(PLAY_TO_CLIENT.buildPacket(Pair.of(buffer, 0), channelName).getThis());
	}
	
	public static void sendToServer(PacketBuffer buffer){
		PacketDistributor.SERVER.noArg().send(PLAY_TO_SERVER.buildPacket(Pair.of(buffer, 0), channelName).getThis());
	}
	
	public interface INetworkHandler{
		void onPacket(ByteBuf data, EntityPlayer player);
	}
}
