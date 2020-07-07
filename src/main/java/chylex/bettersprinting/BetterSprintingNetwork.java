package chylex.bettersprinting;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.ClientCustomPayloadEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkEvent.ServerCustomPayloadEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public final class BetterSprintingNetwork{
	private static final ResourceLocation channelName = new ResourceLocation("bsm", "settings");
	private static final String protocolId = "1";
	
	public static void initialize(INetworkHandler handler){
		NetworkRegistry.newEventChannel(channelName, () -> protocolId, protocolServer -> true, protocolClient -> true).registerObject(new BetterSprintingNetwork(handler));
	}
	
	private final INetworkHandler handler;
	
	private BetterSprintingNetwork(INetworkHandler handler){
		this.handler = handler;
	}
	
	@SubscribeEvent
	public void onServerToClientPacket(ServerCustomPayloadEvent e){
		handlePacket(LogicalSide.CLIENT, getClientPlayer(), e.getPayload().copy(), e.getSource().get());
	}
	
	@SubscribeEvent
	public void onClientToServerPacket(ClientCustomPayloadEvent e){
		Context ctx = e.getSource().get();
		handlePacket(LogicalSide.SERVER, ctx.getSender(), e.getPayload().copy(), ctx);
	}
	
	private void handlePacket(LogicalSide side, PlayerEntity player, ByteBuf payload, Context ctx){
		ctx.enqueueWork(() -> handler.onPacket(side, payload, player));
	}
	
	@OnlyIn(Dist.CLIENT)
	private PlayerEntity getClientPlayer(){
		return Minecraft.getInstance().player;
	}
	
	public static void sendToPlayer(PacketBuffer buffer, PlayerEntity player){
		PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player).send(PLAY_TO_CLIENT.buildPacket(Pair.of(buffer, 0), channelName).getThis());
	}
	
	public static void sendToServer(PacketBuffer buffer){
		PacketDistributor.SERVER.noArg().send(PLAY_TO_SERVER.buildPacket(Pair.of(buffer, 0), channelName).getThis());
	}
	
	public interface INetworkHandler{
		void onPacket(LogicalSide side, ByteBuf data, PlayerEntity player);
		
		static PacketBuffer buf(){
			return new PacketBuffer(Unpooled.buffer());
		}
	}
}
