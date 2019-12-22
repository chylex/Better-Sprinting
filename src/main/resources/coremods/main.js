function initializeCoreMod(){
    var api = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var opcodes = Java.type("org.objectweb.asm.Opcodes");

    api.loadFile("coremods/utils/constants.js");
    api.loadFile("coremods/utils/helpers.js");

    // Transformers

    var transformMovementInputUpdate = function(method){
        print("Transforming livingTick (movement update)...");

        var instructions = method.instructions;
        var entry = null;

        for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
            if (checkInstruction(instructions.get(index), opcodes.INVOKEVIRTUAL, "func_225607_a_") &&
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

        var toRemove = instructions.get(entry - 3);
        var toReplace = instructions.get(entry);

        return function(){
            var call = api.buildMethodCall("chylex/bettersprinting/client/player/LivingUpdate", "injectMovementInputUpdate", "(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Z)V", api.MethodType.STATIC);

            instructions.remove(toRemove);
            instructions.set(toReplace, call);
        };
    };
    
    var transformSprinting = function(method){
        print("Transforming livingTick (sprinting)...");
        
        var instructions = method.instructions;
        var bounds = null;

        for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
            if (checkInstruction(instructions.get(index), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index - 22), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index - 44), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index - 66), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index + 198), opcodes.INVOKEVIRTUAL, "setSprinting", "func_70031_b")
            ){
                bounds = [ index + 1, index + 199 ];
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

        return function(){
            var helper = api.getMethodNode();
            helper.visitMethodInsn(opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectSprinting", "()Z", false);
            helper.visitJumpInsn(opcodes.IFNE, getSkipInst(labels[1]));

            instructions.insert(labels[0], helper.instructions);
        };
    };
    
    var transformAfterSuperCall = function(method){
        print("Transforming livingTick (super call)...");
        
        var instructions = method.instructions;
        var bounds = null;
        
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

        return function(){
            var helper = api.getMethodNode();
            helper.visitMethodInsn(opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectAfterSuperCall", "()Z", false);
            helper.visitJumpInsn(opcodes.IFNE, getSkipInst(labels[1]));

            instructions.insert(labels[0], helper.instructions);
        };
    };

    // Execution

    return {
        "BetterSprintingCore": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.entity.player.ClientPlayerEntity",
                "methodName": "func_70636_d",
                "methodDesc": "()V"
            },
            "transformer": function(methodNode){
                print("Setting up BetterSprintingCore...");

                if (!transformAll(methodNode, [ transformMovementInputUpdate, transformSprinting, transformAfterSuperCall ])){
                    print("Could not inject into ClientPlayerEntity.livingTick(), printing all instructions...");
                    printInstructions(methodNode.instructions);
                }

                // printInstructions(methodNode.instructions);

                print("Finished BetterSprintingCore.");
                return methodNode;
            }
        }
    };
}
