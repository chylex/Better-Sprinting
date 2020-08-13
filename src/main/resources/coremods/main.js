function initializeCoreMod(){
    var api = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    
    api.loadFile("coremods/utils/constants.js");
    api.loadFile("coremods/utils/helpers.js");
    
    // Transformers
    
    api.loadFile("coremods/transform.movementupdate.js");
    api.loadFile("coremods/transform.sprinting.js");
    api.loadFile("coremods/transform.groundflight.js");
    
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
