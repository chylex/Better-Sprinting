package chylex.bettersprinting.system.core;
import static org.objectweb.asm.Opcodes.*;
import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import chylex.bettersprinting.system.Log;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public final class TransformerEntityPlayerSP implements IClassTransformer{
	private static final String[] NAMES_ONLIVINGUPDATE = new String[]{ "e", "onLivingUpdate" };
	private static final String DESC_ONLIVINGUPDATE = "()V";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes){
		if (transformedName.equals("net.minecraft.client.entity.EntityPlayerSP")){
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(node, 0);
			
			transformEntityPlayerSP(node);
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		}
		
		return bytes;
	}
	
	private void transformEntityPlayerSP(ClassNode node){
		MethodNode onLivingUpdate = node.methods
			.stream()
			.filter(method -> method.desc.equals(DESC_ONLIVINGUPDATE) && ArrayUtils.contains(NAMES_ONLIVINGUPDATE, method.name))
			.findAny()
			.<IllegalStateException>orElseThrow(() -> {
				logMethods("onLivingUpdate", node);
				return new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP - could not find onLivingUpdate. The mod has generated logs to help pinpointing the issue, please include them in your report.");
			});
		
		try{
			transformOnLivingUpdate(onLivingUpdate);
			transformOnLivingUpdateEnd(onLivingUpdate);
		}catch(Throwable t){
			logInstructions(onLivingUpdate);
			throw new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP. The mod has generated logs to help pinpointing the issue, please include them in your report.", t);
		}
	}
	
	// EntityPlayerSP.onLivingUpdate()
	
	private void transformOnLivingUpdate(MethodNode method){
		InsnList instructions = method.instructions;
		int instrcount = instructions.size();
		
		int insertionPoint = -1;
		int skipPoint = -1;
		
		String[] clsMovementInput = getClassNames("net/minecraft/util/MovementInput");
		String[] mtdIsRidingHorse = new String[]{ "isRidingHorse", "u" };
		
		for(int index = 0; index < instrcount; index++){
			AbstractInsnNode instruction = instructions.get(index);
			
			if (instruction.getOpcode() == GETFIELD &&
				ArrayUtils.contains(clsMovementInput, ((FieldInsnNode)instruction).desc) &&
				checkOpcodeChain(instructions, index - 1, new int[]{ ALOAD, GETFIELD, GETFIELD, ISTORE }) &&
				checkOpcodeChain(instructions, index + 5, new int[]{ LDC, FSTORE })
			){
				insertionPoint = index + 3;
				break;
			}
		}
		
		if (insertionPoint == -1){
			throw new IllegalStateException("Could not find insertion point.");
		}
		
		Log.debug("Found insertion point at index: $0", insertionPoint);
		
		for(int index = insertionPoint; index < instrcount; index++){
			AbstractInsnNode instruction = instructions.get(index);
			
			if (instruction.getOpcode() == INVOKEVIRTUAL){
				MethodInsnNode methodInsn = (MethodInsnNode)instruction;
				
				if (ArrayUtils.contains(mtdIsRidingHorse, methodInsn.name) && methodInsn.desc.equals("()Z")){
					skipPoint = index - 4;
					break;
				}
			}
		}
		
		if (skipPoint == -1){
			throw new IllegalStateException("Could not find skip point.");
		}
		
		Log.debug("Found skip point at index: $0", insertionPoint);
		
		AbstractInsnNode insertNode = instructions.get(insertionPoint);
		AbstractInsnNode skipNode = instructions.get(skipPoint);
		
		if (!(insertNode instanceof LabelNode)){
			throw new IllegalStateException("invalid insertion point node, expected label, got " + insertNode.getClass().getSimpleName());
		}
		
		if (!(skipNode instanceof LabelNode)){
			throw new IllegalStateException("invalid insertion point node, expected label, got " + skipNode.getClass().getSimpleName());
		}
		
		InsnList inserted = new InsnList();
		inserted.add(new VarInsnNode(ALOAD, 0));
		inserted.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectOnLivingUpdate", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
		inserted.add(new JumpInsnNode(Opcodes.GOTO, (LabelNode)skipNode));
		
		instructions.insert(insertNode, inserted);
	}
	
	private void transformOnLivingUpdateEnd(MethodNode method){
		InsnList instructions = method.instructions;
		int instrcount = instructions.size();
		
		int insertionPoint = -1;
		int skipPoint = -1;
		
		String[] mtdSendPlayerAbilities = new String[]{ "sendPlayerAbilities", "q" };
		
		for(int index = instrcount - 1; index >= 0; index--){
			AbstractInsnNode instruction = instructions.get(index);
			
			if (instruction.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)instruction).name.equals(method.name)){
				insertionPoint = index + 1;
				break;
			}
		}
		
		if (insertionPoint == -1){
			throw new IllegalStateException("Could not find insertion point.");
		}
		
		Log.debug("Found insertion point at index: $0", insertionPoint);
		
		for(int index = insertionPoint; index < instrcount; index++){
			AbstractInsnNode instruction = instructions.get(index);
			
			if (instruction.getOpcode() == INVOKEVIRTUAL){
				MethodInsnNode methodInsn = (MethodInsnNode)instruction;
				
				if (ArrayUtils.contains(mtdSendPlayerAbilities, methodInsn.name) && methodInsn.desc.equals("()V")){
					skipPoint = index + 1;
					break;
				}
			}
		}
		
		if (skipPoint == -1){
			throw new IllegalStateException("Could not find skip point.");
		}
		
		Log.debug("Found skip point at index: $0", insertionPoint);
		
		AbstractInsnNode insertNode = instructions.get(insertionPoint);
		AbstractInsnNode skipNode = instructions.get(skipPoint);
		
		if (!(insertNode instanceof LabelNode)){
			throw new IllegalStateException("invalid insertion point node, expected label, got " + insertNode.getClass().getSimpleName());
		}
		
		if (!(skipNode instanceof LabelNode)){
			throw new IllegalStateException("invalid insertion point node, expected label, got " + skipNode.getClass().getSimpleName());
		}
		
		InsnList inserted = new InsnList();
		inserted.add(new VarInsnNode(ALOAD, 0));
		inserted.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectOnLivingUpdateEnd", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
		inserted.add(new JumpInsnNode(Opcodes.GOTO, (LabelNode)skipNode));
		
		instructions.insert(insertNode, inserted);
	}
	
	// Helpers
	
	private static String[] getClassNames(String name){
		String obf = FMLDeobfuscatingRemapper.INSTANCE.unmap(name);
		
		return new String[]{
			"L" + name + ";",
			"L" + obf + ";"
		};
	}
	
	private static boolean checkOpcodeChain(InsnList instructions, int start, int[] chain){
		for(int offset = 0; offset < chain.length; offset++){
			AbstractInsnNode instruction = instructions.get(start + offset);
			
			if (instruction.getOpcode() != chain[offset]){
				Log.warn("Mismatched opcode chain, $0 != $1", instruction.getOpcode(), chain[offset]);
				return false;
			}
		}
		
		return true;
	}
	
	private static void logMethods(String missingMethod, ClassNode owner){
		Log.error("Better Sprinting could not find EntityPlayerSP.$0, generating debug logs...", missingMethod);
		
		for(MethodNode method:owner.methods){
			Log.error("> $0 .. $1", method.name, method.desc);
		}
	}
	
	private static void logInstructions(MethodNode method){
		TraceMethodVisitor visitor = new TraceMethodVisitor(new Textifier());
		
		for(Iterator<AbstractInsnNode> iter = method.instructions.iterator(); iter.hasNext();){
			iter.next().accept(visitor);
		}
		
		int index = 0;
		
		for(Object obj:visitor.p.getText()){
			Log.error("> $0: $1", ++index, StringUtils.stripEnd(obj.toString(), null));
		}
	}
}
