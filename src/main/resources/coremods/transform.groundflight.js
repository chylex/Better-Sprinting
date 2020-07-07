var transformAfterSuperCall = function(api, opcodes, method){
    print("Transforming livingTick (super call)...");

    var instructions = method.instructions;
    var bounds = null;

    /*
     *   | L119
     *   |  LINENUMBER
     *   | FRAME SAME
     *   |  ALOAD 0
     * 1 |  INVOKESPECIAL net/minecraft/client/entity/player/AbstractClientPlayerEntity.livingTick ()V
     * A | L120
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.onGround : Z
     *   |  IFEQ L121
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.abilities : Lnet/minecraft/entity/player/PlayerAbilities;
     *   |  GETFIELD net/minecraft/entity/player/PlayerAbilities.isFlying : Z
     *   |  IFEQ L121
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.mc : Lnet/minecraft/client/Minecraft;
     *   |  GETFIELD net/minecraft/client/Minecraft.playerController : Lnet/minecraft/client/multiplayer/PlayerController;
     *   |  INVOKEVIRTUAL net/minecraft/client/multiplayer/PlayerController.isSpectatorMode ()Z
     *   |  IFNE L121
     *   | L122
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.abilities : Lnet/minecraft/entity/player/PlayerAbilities;
     *   |  ICONST_0
     *   |  PUTFIELD net/minecraft/entity/player/PlayerAbilities.isFlying : Z
     *   | L123
     *   |  LINENUMBER
     *   |  ALOAD 0
     * 1 |  INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.sendPlayerAbilities ()V
     * B | L121
     */

    for(var index = instructions.size() - 1; index >= 0; index--){
        if (checkInstruction(instructions.get(index), opcodes.INVOKESPECIAL, "livingTick", "func_70636_d") &&
            checkInstruction(instructions.get(index + 24), opcodes.INVOKEVIRTUAL, "sendPlayerAbilities", "func_71016_p")
        ){
            bounds = [ index + 1, index + 25 ];
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
     *   | L120
     * + |  INVOKESTATIC chylex/bettersprinting/client/player/LivingUpdate.injectAfterSuperCall ()Z
     * + |  IFNE <B>
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.onGround : Z
     *   |  IFEQ L121
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.abilities : Lnet/minecraft/entity/player/PlayerAbilities;
     *   |  GETFIELD net/minecraft/entity/player/PlayerAbilities.isFlying : Z
     *   |  IFEQ L121
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.mc : Lnet/minecraft/client/Minecraft;
     *   |  GETFIELD net/minecraft/client/Minecraft.playerController : Lnet/minecraft/client/multiplayer/PlayerController;
     *   |  INVOKEVIRTUAL net/minecraft/client/multiplayer/PlayerController.isSpectatorMode ()Z
     *   |  IFNE L121
     *   | L122
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.abilities : Lnet/minecraft/entity/player/PlayerAbilities;
     *   |  ICONST_0
     *   |  PUTFIELD net/minecraft/entity/player/PlayerAbilities.isFlying : Z
     *   | L123
     *   |  LINENUMBER
     *   |  ALOAD 0
     *   |  INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.sendPlayerAbilities ()V
     * B | L121
     */

    return function(){
        var helper = api.getMethodNode();
        helper.visitMethodInsn(opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectAfterSuperCall", "()Z", false);
        helper.visitJumpInsn(opcodes.IFNE, getSkipInst(labels[1]));

        instructions.insert(labels[0], helper.instructions);
    };
};
