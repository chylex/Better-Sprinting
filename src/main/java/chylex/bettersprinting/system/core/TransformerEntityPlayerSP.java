package chylex.bettersprinting.system.core;
import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import chylex.bettersprinting.system.Log;

public final class TransformerEntityPlayerSP implements IClassTransformer{
	private static final String[] NAMES_ONLIVINGUPDATE = new String[]{ "n", "onLivingUpdate" };
	private static final String DESC_ONLIVINGUPDATE = "()V";
	private static final String SRG_ONLIVINGUPDATE = "func_70636_d";
	
	private static final String[] NAMES_PUSHOUTOFBLOCKS = new String[]{ "i", "pushOutOfBlocks" };
	private static final String DESC_PUSHOUTOFBLOCKS = "(DDD)Z";
	private static final String SRG_PUSHOUTOFBLOCKS = "func_145771_j";
	
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
		if (BetterSprintingCore.transformOnLivingUpdate){
			transformOnLivingUpdate(node.methods
				.stream()
				.filter(method -> method.desc.equals(DESC_ONLIVINGUPDATE) && ArrayUtils.contains(NAMES_ONLIVINGUPDATE, method.name))
				.findAny()
				.<IllegalStateException>orElseThrow(() -> {
					logMethods("onLivingUpdate", node);
					return new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP - could not find onLivingUpdate. The mod has generated logs to help pinpointing the issue, please include them in your report. You can also try downloading PlayerAPI to resolve the issue.");
				})
			);
		}
		
		node.methods.add(bridgePushOutOfBlocks(node.methods
			.stream()
			.filter(method -> method.desc.equals(DESC_PUSHOUTOFBLOCKS) && ArrayUtils.contains(NAMES_PUSHOUTOFBLOCKS, method.name))
			.findAny()
			.<IllegalStateException>orElseThrow(() -> {
				logMethods("pushOutOfBlocks", node);
				throw new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP - could not find pushOutOfBlocks. The mod has generated logs to help pinpointing the issue, please include them in your report.");
			})
		));
	}
	
	private int findInsertionPointOnLivingUpdate(MethodNode method){
		String[] clsMovementInput = getClassNames("net/minecraft/util/MovementInput", true);
		
		for(int index = 0, count = method.instructions.size(), lastLabelIndex = -1; index < count; index++){
			AbstractInsnNode node = method.instructions.get(index);
			
			if (node instanceof LabelNode){
				lastLabelIndex = index;
			}
			else if (node instanceof FieldInsnNode && ArrayUtils.contains(clsMovementInput, ((FieldInsnNode)node).desc)){
				return lastLabelIndex;
			}
		}
		
		Log.error("Finding insertion point - $0", String.join(" / ", clsMovementInput));
		logInstructions(method);
		
		throw new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP - could not find an insertion point into onLivingUpdate. The mod has generated logs to help pinpointing the issue, please include them in your report. You can also try downloading PlayerAPI to resolve the issue.");
	}
	
	/*private int findSkipPointOnLivingUpdate(MethodNode method, int insertionPoint){
		String[] clsIJumpingMount = getClassNames("net/minecraft/entity/IJumpingMount", false);
		
		for(int index = method.instructions.size()-1; index >= insertionPoint; index--){
			AbstractInsnNode node = method.instructions.get(index);
			
			if (node instanceof TypeInsnNode && ArrayUtils.contains(clsIJumpingMount, ((TypeInsnNode)node).desc)){
				while(--index >= insertionPoint && !(method.instructions.get(index) instanceof LabelNode));
				while(--index >= insertionPoint && !(method.instructions.get(index) instanceof LabelNode));
				return index;
			}
		}
		
		Log.error("Finding skip point - $0", String.join(" / ", clsIJumpingMount));
		logInstructions(method);
		
		throw new IllegalStateException("Better Sprinting failed modifying EntityPlayerSP - could not find a skip point in onLivingUpdate. The mod has generated logs to help pinpointing the issue, please include them in your report. You can also try downloading PlayerAPI to resolve the issue.");
	}*/
	
	private void transformOnLivingUpdate(MethodNode method){
		int insertionPoint = findInsertionPointOnLivingUpdate(method);
		
		String targetName = method.name.equals("onLivingUpdate") ? method.name : SRG_ONLIVINGUPDATE;
		InsnList instructions = new InsnList();
		
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "callPreSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer", targetName, method.desc, false));

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "callPostSuper", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
		
		instructions.add(new JumpInsnNode(Opcodes.GOTO, (LabelNode)method.instructions.getLast()));
		
		method.instructions.insert(method.instructions.get(insertionPoint), instructions);
		
		for(int index = insertionPoint+instructions.size()+1; index < method.instructions.size(); index++){
			AbstractInsnNode node = method.instructions.get(index);
			
			if (node instanceof LabelNode){
				method.instructions.insert(node, new InsnNode(Opcodes.RETURN));
			}
		}
		
		if (Log.isDeobfEnvironment){
			logInstructions(method);
		}
	}
	
	private MethodNode bridgePushOutOfBlocks(MethodNode target){
		String targetName = target.name.equals("pushOutOfBlocks") ? target.name : SRG_PUSHOUTOFBLOCKS;
		MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC, "_bsm_pushOutOfBlocks", "(DDD)V", null, null);
		
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 5));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", targetName, target.desc, false));
		m.instructions.add(new InsnNode(Opcodes.POP));
		m.instructions.add(new InsnNode(Opcodes.RETURN));
		
		return m;
	}
	
	private static String[] getClassNames(String name, boolean full){
		String obf = FMLDeobfuscatingRemapper.INSTANCE.unmap(name);
		
		return new String[]{
			full ? "L"+name+";" : name,
			full ? "L"+obf+";" : obf
		};
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
