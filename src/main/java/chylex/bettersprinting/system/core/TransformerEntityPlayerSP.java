package chylex.bettersprinting.system.core;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class TransformerEntityPlayerSP implements IClassTransformer{
	private static final String METHOD_NAME = BetterSprintingCore.isObfuscated ? "func_70636_d" : "onLivingUpdate";
	private static final String METHOD_DESC = "()V";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes){
		if (BetterSprintingCore.allowTransform && transformedName.equals("net.minecraft.client.entity.EntityPlayerSP")){
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(node, 0);
			
			transformClass(node);
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			return writer.toByteArray();
		}
		
		return bytes;
	}
	
	private void transformClass(ClassNode node){
		for(MethodNode method:node.methods){
			if (method.name.equals(METHOD_NAME) && method.desc.equals(METHOD_DESC)){
				transformMethod(method);
				break;
			}
		}
	}
	
	private void transformMethod(MethodNode method){
		InsnList instructions = new InsnList();
		
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/impl/LivingUpdate", "callPreSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer", METHOD_NAME, "()V", false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/impl/LivingUpdate", "callPostSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
		
		instructions.add(new InsnNode(Opcodes.RETURN));
		
		method.instructions.insertBefore(method.instructions.getFirst(), instructions);
	}
}
