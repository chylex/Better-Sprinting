var transformSprinting = function(api, opcodes, method){
    print("Transforming livingTick (sprinting)...");

    var instructions = method.instructions;
    var bounds = null;

    /*
     * 1 |  INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.pushOutOfBlocks (DDD)V
     * A | L25
     *   |  LINENUMBER
     *   | FRAME APPEND [net/minecraftforge/client/event/PlayerSPPushOutOfBlocksEvent]
     *   |  ILOAD 2
     *   |  IFEQ L30
     *   | L31
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  ICONST_0
     *   |  PUTFIELD net/minecraft/client/entity/player/ClientPlayerEntity.sprintToggleTimer : I
     *   :
     *   :
     *   :
     *   | L58
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  ICONST_0
     * 1 |  INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.setSprinting (Z)V
     * B | L44
     *   |  LINENUMBER
     *   | FRAME CHOP 2
     *   |  ICONST_0
     *   |  ISTORE 7
     *   | L59
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.abilities : Lnet/minecraft/entity/player/PlayerAbilities;
     *   |  GETFIELD net/minecraft/entity/player/PlayerAbilities.allowFlying : Z
     *   |  IFEQ L60
     */

    for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
        if (checkInstruction(instructions.get(index), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
            checkInstruction(instructions.get(index - 22), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
            checkInstruction(instructions.get(index - 44), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
            checkInstruction(instructions.get(index - 66), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
            checkInstruction(instructions.get(index + 208), opcodes.INVOKEVIRTUAL, "setSprinting", "func_70031_b")
        ){
            bounds = [ index + 1, index + 209 ];
            break;
        }
    }

    if (bounds === null){
        return false;
    }

    print("Found insertion point at " + bounds[0] + ", skip point at " + bounds[1] + ".");

    var labels = [ instructions.get(bounds[0]), instructions.get(bounds[1]) ];

    if (!validateLabels(labels)){
        return false;
    }

    /*
     *   | L25
     * + |  INVOKESTATIC chylex/bettersprinting/client/player/LivingUpdate.injectSprinting ()Z
     * + |  IFNE <B>
     *   |  LINENUMBER
     *   | FRAME APPEND [net/minecraftforge/client/event/PlayerSPPushOutOfBlocksEvent]
     *   |  ILOAD 2
     *   |  IFEQ L30
     *   :
     *   :
     *   :
     *   | L58
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  ICONST_0
     *   |  INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.setSprinting (Z)V
     * B | L44
     */

    return function(){
        var helper = api.getMethodNode();
        helper.visitMethodInsn(opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectSprinting", "()Z", false);
        helper.visitJumpInsn(opcodes.IFNE, getSkipInst(labels[1]));

        instructions.insert(labels[0], helper.instructions);
    };
};
