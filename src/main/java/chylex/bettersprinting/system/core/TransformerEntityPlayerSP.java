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
	private static final String NAME_ONLIVINGUPDATE = BetterSprintingCore.isObfuscated ? "func_70636_d" : "onLivingUpdate";
	private static final String DESC_ONLIVINGUPDATE = "()V";
	
	private static final String NAME_PUSHOUTOFBLOCKS = BetterSprintingCore.isObfuscated ? "func_145771_j" : "pushOutOfBlocks";
	private static final String DESC_PUSHOUTOFBLOCKS = "(DDD)Z";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes){
		if (transformedName.equals("net.minecraft.client.entity.EntityPlayerSP")){
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(node, 0);
			
			transformEntityPlayerSP(node);
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			return writer.toByteArray();
		}
		
		return bytes;
	}
	
	private void transformEntityPlayerSP(ClassNode node){
		if (BetterSprintingCore.transformOnLivingUpdate){
			for(MethodNode method:node.methods){
				if (method.name.equals(NAME_ONLIVINGUPDATE) && method.desc.equals(DESC_ONLIVINGUPDATE)){
					transformOnLivingUpdate(method);
					break;
				}
			}
		}
		
		node.methods.add(createPushOutOfBlocks());
	}
	
	private void transformOnLivingUpdate(MethodNode method){
		InsnList instructions = new InsnList();
		
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "callPreSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer", NAME_ONLIVINGUPDATE, DESC_ONLIVINGUPDATE, false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "callPostSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
		
		instructions.add(new InsnNode(Opcodes.RETURN));
		
		method.instructions.insertBefore(method.instructions.getFirst(), instructions);
	}
	
	private MethodNode createPushOutOfBlocks(){
		MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC, "_bsm_pushOutOfBlocks", "(DDD)V", null, null);
		
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 5));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", NAME_PUSHOUTOFBLOCKS, DESC_PUSHOUTOFBLOCKS, false));
		m.instructions.add(new InsnNode(Opcodes.POP));
		m.instructions.add(new InsnNode(Opcodes.RETURN));
		
		return m;
	}
}
