package chylex.bettersprinting.system.core;
import chylex.bettersprinting.system.Log;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import java.util.Iterator;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public final class TransformerEntityPlayerSP implements IClassTransformer{
	private static final String[] NAMES_ONLIVINGUPDATE = new String[]{ "n", "onLivingUpdate" };
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
			transformMovementInputUpdate(onLivingUpdate);
			transformSprinting(onLivingUpdate);
			transformAfterSuperCall(onLivingUpdate);
		}catch(Throwable t){
			logInstructions(onLivingUpdate);
			throw new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP. The mod has generated logs to help pinpointing the issue, please include them in your report.", t);
		}
	}
	
	// EntityPlayerSP.onLivingUpdate()
	
	private void transformMovementInputUpdate(MethodNode method){
		Log.debug("Transforming onLivingUpdate (movement update)...");
		
		InsnList instructions = method.instructions;
		int entry = -1;
		
		for(int index = 0, instrcount = instructions.size(); index < instrcount; index++){
			if (checkMethodInstruction(instructions.get(index), INVOKEVIRTUAL, "updatePlayerMoveState", "a") &&
				checkOpcodeChain(instructions, index - 2, new int[]{ ALOAD, GETFIELD })
            ){
				entry = index;
				break;
			}
		}
		
		if (entry == -1){
			throw new IllegalStateException("Could not find entry point.");
		}
		
		Log.debug("Found entry point at " + entry + ".");
		
		AbstractInsnNode toRemove = instructions.get(entry - 1);
		AbstractInsnNode toReplace = instructions.get(entry);
		
		instructions.remove(toRemove);
		instructions.set(toReplace, new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectMovementInputUpdate", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
	}
	
	private void transformSprinting(MethodNode method){
		Log.debug("Transforming onLivingUpdate (sprinting)...");
		
		InsnList instructions = method.instructions;
		int[] bounds = null;
		
		for(int index = 0, instrcount = instructions.size(); index < instrcount; index++){
			if (checkMethodInstruction(instructions.get(index), INVOKEVIRTUAL, "pushOutOfBlocks", "i") &&
				checkMethodInstruction(instructions.get(index - 25), INVOKEVIRTUAL, "pushOutOfBlocks", "i") &&
				checkMethodInstruction(instructions.get(index - 50), INVOKEVIRTUAL, "pushOutOfBlocks", "i") &&
				checkMethodInstruction(instructions.get(index - 75), INVOKEVIRTUAL, "pushOutOfBlocks", "i") &&
				checkMethodInstruction(instructions.get(index + 130), INVOKEVIRTUAL, "setSprinting", "f")
			){
				bounds = new int[]{ index + 2, index + 131 };
				break;
			}
		}
		
		if (bounds == null){
			throw new IllegalStateException("Could not find entry point.");
		}
		
		Log.debug("Found insertion point at " + bounds[0] + ", skip point at " + bounds[1] + ".");
		
		AbstractInsnNode[] labels = new AbstractInsnNode[]{ instructions.get(bounds[0]), instructions.get(bounds[1]) };
		validateLabels(labels[0], labels[1]);
		
		InsnList inserted = new InsnList();
		inserted.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectSprinting", "()Z", false));
		inserted.add(new JumpInsnNode(Opcodes.IFNE, (LabelNode)labels[1]));
		
		instructions.insert(labels[0], inserted);
	}
	
	private void transformAfterSuperCall(MethodNode method){
		Log.debug("Transforming onLivingUpdate (super call)...");
		
		InsnList instructions = method.instructions;
		int[] bounds = null;
		
		for(int index = instructions.size() - 1; index >= 0; index--){
			if (checkMethodInstruction(instructions.get(index), INVOKESPECIAL, method.name, null) &&
				checkMethodInstruction(instructions.get(index + 24), INVOKEVIRTUAL, "sendPlayerAbilities", "w")
			){
				bounds = new int[]{ index + 1, index + 25 };
				break;
			}
		}
		
		if (bounds == null){
			throw new IllegalStateException("Could not find entry point.");
		}
		
		Log.debug("Found insertion point at " + bounds[0] + ", skip point at " + bounds[1] + ".");
		
		AbstractInsnNode[] labels = new AbstractInsnNode[]{ instructions.get(bounds[0]), instructions.get(bounds[1]) };
		validateLabels(labels[0], labels[1]);
		
		InsnList inserted = new InsnList();
		inserted.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectAfterSuperCall", "()Z", false));
		inserted.add(new JumpInsnNode(Opcodes.IFNE, (LabelNode)labels[1]));
		
		instructions.insert(labels[0], inserted);
	}
	
	// Helpers
	
	private static void validateLabels(AbstractInsnNode insertNode, AbstractInsnNode skipNode){
		if (!(insertNode instanceof LabelNode)){
			throw new IllegalStateException("Invalid insertion point node, expected label, got: " + insertNode.getClass().getSimpleName());
		}
		
		if (!(skipNode instanceof LabelNode)){
			throw new IllegalStateException("Invalid insertion point node, expected label, got: " + skipNode.getClass().getSimpleName());
		}
	}
	
	private static boolean checkMethodInstruction(AbstractInsnNode instruction, int opcode, String name1, String name2){
		if (instruction.getOpcode() != opcode){
			return false;
		}
		
		String name = ((MethodInsnNode)instruction).name;
		return name.equals(name1) || name.equals(name2);
	}
	
	private static boolean checkOpcodeChain(InsnList instructions, int start, int[] chain){
		for(int offset = 0; offset < chain.length; offset++){
			AbstractInsnNode instruction = instructions.get(start + offset);
			
			if (instruction.getOpcode() != chain[offset]){
				Log.debug("Mismatched opcode chain, $0 != $1", instruction.getOpcode(), chain[offset]);
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
