var transformMovementInputUpdate = function(api, opcodes, method){
    print("Transforming livingTick (movement update)...");
    
    var instructions = method.instructions;
    var entry = null;
    
    /*
     * 4 | ALOAD 0
     * 3 | GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.movementInput : Lnet/minecraft/util/MovementInput;
     * 2 | ALOAD 0
     * 1 | INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.func_228354_I_ ()Z
     * E | INVOKEVIRTUAL net/minecraft/util/MovementInput.func_225607_a_ (Z)V
     */
    
    for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
        if (checkInstruction(instructions.get(index), opcodes.INVOKEVIRTUAL, "tickMovement", "func_225607_a_") &&
            checkOpcodeChain(instructions, index - 4, [ opcodes.ALOAD, opcodes.GETFIELD, opcodes.ALOAD, opcodes.INVOKEVIRTUAL ])
        ){
            entry = index;
            break;
        }
    }
    
    if (entry === null){
        return false;
    }
    
    print("Found entry point at " + entry + ".");
    
    /*
     *   | ALOAD 0
     * - | GETFIELD net/minecraft/client/entity/player/ClientPlayerEntity.movementInput : Lnet/minecraft/util/MovementInput;
     *   | ALOAD 0
     *   | INVOKEVIRTUAL net/minecraft/client/entity/player/ClientPlayerEntity.func_228354_I_ ()Z
     * - | INVOKEVIRTUAL net/minecraft/util/MovementInput.func_225607_a_ (Z)V
     * + | INVOKESTATIC chylex/bettersprinting/client/player/LivingUpdate.injectMovementInputUpdate (Lnet/minecraft/client/entity/player/ClientPlayerEntity;Z)V
     */
    
    var toRemove = instructions.get(entry - 3);
    var toReplace = instructions.get(entry);
    
    return function(){
        var call = api.buildMethodCall("chylex/bettersprinting/client/player/LivingUpdate", "injectMovementInputUpdate", "(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Z)V", api.MethodType.STATIC);
        
        instructions.remove(toRemove);
        instructions.set(toReplace, call);
    };
};
