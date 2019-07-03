function initializeCoreMod(){
    var api = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var opcodes = Java.type("org.objectweb.asm.Opcodes");

    var getInstructionTypeName = function(instruction){
        var type = instruction.getType();
        
        switch(type){
            case 0: return "Insn";
            case 1: return "IntInsn";
            case 2: return "VarInsn";
            case 3: return "TypeInsn";
            case 4: return "FieldInsn";
            case 5: return "MethodInsn";
            case 6: return "InvokeDynamicInsn";
            case 7: return "JumpInsn";
            case 8: return "Label";
            case 9: return "LdcInsn";
            case 10: return "IincInsn";
            case 11: return "TableSwitchInsn";
            case 12: return "LookupSwitchInsn";
            case 13: return "MultiANewArrayInsn";
            case 14: return "Frame";
            case 15: return "LineNumber";
            default: return "[unknown - " + type + "]";
        }
    };
    
    var getInstructionOpcodeName = function(instruction){
        var opcode = instruction.getOpcode();
        
        switch(opcode){
            case -1: return "";
            case 0: return "NOP";
            case 1: return "ACONST_NULL";
            case 2: return "ICONST_M1";
            case 3: return "ICONST_0";
            case 4: return "ICONST_1";
            case 5: return "ICONST_2";
            case 6: return "ICONST_3";
            case 7: return "ICONST_4";
            case 8: return "ICONST_5";
            case 9: return "LCONST_0";
            case 10: return "LCONST_1";
            case 11: return "FCONST_0";
            case 12: return "FCONST_1";
            case 13: return "FCONST_2";
            case 14: return "DCONST_0";
            case 15: return "DCONST_1";
            case 16: return "BIPUSH";
            case 17: return "SIPUSH";
            case 18: return "LDC";
            case 21: return "ILOAD";
            case 22: return "LLOAD";
            case 23: return "FLOAD";
            case 24: return "DLOAD";
            case 25: return "ALOAD";
            case 46: return "IALOAD";
            case 47: return "LALOAD";
            case 48: return "FALOAD";
            case 49: return "DALOAD";
            case 50: return "AALOAD";
            case 51: return "BALOAD";
            case 52: return "CALOAD";
            case 53: return "SALOAD";
            case 54: return "ISTORE";
            case 55: return "LSTORE";
            case 56: return "FSTORE";
            case 57: return "DSTORE";
            case 58: return "ASTORE";
            case 79: return "IASTORE";
            case 80: return "LASTORE";
            case 81: return "FASTORE";
            case 82: return "DASTORE";
            case 83: return "AASTORE";
            case 84: return "BASTORE";
            case 85: return "CASTORE";
            case 86: return "SASTORE";
            case 87: return "POP";
            case 88: return "POP2";
            case 89: return "DUP";
            case 90: return "DUP_X1";
            case 91: return "DUP_X2";
            case 92: return "DUP2";
            case 93: return "DUP2_X1";
            case 94: return "DUP2_X2";
            case 95: return "SWAP";
            case 96: return "IADD";
            case 97: return "LADD";
            case 98: return "FADD";
            case 99: return "DADD";
            case 100: return "ISUB";
            case 101: return "LSUB";
            case 102: return "FSUB";
            case 103: return "DSUB";
            case 104: return "IMUL";
            case 105: return "LMUL";
            case 106: return "FMUL";
            case 107: return "DMUL";
            case 108: return "IDIV";
            case 109: return "LDIV";
            case 110: return "FDIV";
            case 111: return "DDIV";
            case 112: return "IREM";
            case 113: return "LREM";
            case 114: return "FREM";
            case 115: return "DREM";
            case 116: return "INEG";
            case 117: return "LNEG";
            case 118: return "FNEG";
            case 119: return "DNEG";
            case 120: return "ISHL";
            case 121: return "LSHL";
            case 122: return "ISHR";
            case 123: return "LSHR";
            case 124: return "IUSHR";
            case 125: return "LUSHR";
            case 126: return "IAND";
            case 127: return "LAND";
            case 128: return "IOR";
            case 129: return "LOR";
            case 130: return "IXOR";
            case 131: return "LXOR";
            case 132: return "IINC";
            case 133: return "I2L";
            case 134: return "I2F";
            case 135: return "I2D";
            case 136: return "L2I";
            case 137: return "L2F";
            case 138: return "L2D";
            case 139: return "F2I";
            case 140: return "F2L";
            case 141: return "F2D";
            case 142: return "D2I";
            case 143: return "D2L";
            case 144: return "D2F";
            case 145: return "I2B";
            case 146: return "I2C";
            case 147: return "I2S";
            case 148: return "LCMP";
            case 149: return "FCMPL";
            case 150: return "FCMPG";
            case 151: return "DCMPL";
            case 152: return "DCMPG";
            case 153: return "IFEQ";
            case 154: return "IFNE";
            case 155: return "IFLT";
            case 156: return "IFGE";
            case 157: return "IFGT";
            case 158: return "IFLE";
            case 159: return "IF_ICMPEQ";
            case 160: return "IF_ICMPNE";
            case 161: return "IF_ICMPLT";
            case 162: return "IF_ICMPGE";
            case 163: return "IF_ICMPGT";
            case 164: return "IF_ICMPLE";
            case 165: return "IF_ACMPEQ";
            case 166: return "IF_ACMPNE";
            case 167: return "GOTO";
            case 168: return "JSR";
            case 169: return "RET";
            case 170: return "TABLESWITCH";
            case 171: return "LOOKUPSWITCH";
            case 172: return "IRETURN";
            case 173: return "LRETURN";
            case 174: return "FRETURN";
            case 175: return "DRETURN";
            case 176: return "ARETURN";
            case 177: return "RETURN";
            case 178: return "GETSTATIC";
            case 179: return "PUTSTATIC";
            case 180: return "GETFIELD";
            case 181: return "PUTFIELD";
            case 182: return "INVOKEVIRTUAL";
            case 183: return "INVOKESPECIAL";
            case 184: return "INVOKESTATIC";
            case 185: return "INVOKEINTERFACE";
            case 186: return "INVOKEDYNAMIC";
            case 187: return "NEW";
            case 188: return "NEWARRAY";
            case 189: return "ANEWARRAY";
            case 190: return "ARRAYLENGTH";
            case 191: return "ATHROW";
            case 192: return "CHECKCAST";
            case 193: return "INSTANCEOF";
            case 194: return "MONITORENTER";
            case 195: return "MONITOREXIT";
            case 197: return "MULTIANEWARRAY";
            case 198: return "IFNULL";
            case 199: return "IFNONNULL";
            default: return "[unknown - " + opcode + "]";
        }
    };
    
    var printInstructions = function(instructions){
        for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
            var instruction = instructions.get(index);
            
            var indexStr = index + ": ";
            var typeName = getInstructionTypeName(instruction);
            var opcodeName = getInstructionOpcodeName(instruction);
            
            while(indexStr.length() < 6){
                indexStr = " " + indexStr;
            }
            
            while(typeName.length() < 12){
                typeName = typeName + " ";
            }
            
            if (opcodeName.length() > 0){
                opcodeName = " | " + opcodeName;
                
                try{
                    var name = instruction.name;
                    
                    if (name){
                        opcodeName += ", " + name;
                    }
                }catch(e){}
                
                try{
                    var desc = instruction.desc;
                    
                    if (desc){
                        opcodeName += ", " + desc;
                    }
                }catch(e){}

                try{
                    var label = instruction.label;

                    if (label){
                        for(var search = 0; search < instrcount; search++){
                            if (instructions.get(search) == label){
                                opcodeName += ", " + search;
                                break;
                            }
                        }
                    }
                }catch(e){}
            }
            
            print(indexStr + typeName + opcodeName);
        }
    };

    // Helpers
    
    var checkInstruction = function(instruction, opcode, name1, name2){
        return instruction.getOpcode() === opcode && (instruction.name.equals(name1) || (name2 && instruction.name.equals(name2)));
    };
    
    var checkOpcodeChain = function(instructions, start, chain){
        for(var offset = 0; offset < chain.length; offset++){
            var instruction = instructions.get(start + offset);
            
            if (instruction.getOpcode() !== chain[offset]){
                print("Mismatched opcode chain, " + instruction.getOpcode() + " != " + chain[offset]);
                return false;
            }
        }
        
        return true;
    };

    var validateLabels = function(labels){
        if (labels[0].getType() != 8){
            print("Insertion point is not a label!");
            return false;
        }

        if (labels[1].getType() != 8){
            print("Skip point is not a label!");
            return false;
        }

        return true;
    };

    var getSkipInst = function(label){
        var labelInst = label.getLabel();
        labelInst.info = label;
        return labelInst;
    };

    // Transformers

    var transformMovementInputUpdate = function(method){
        print("Transforming livingTick (movement update)...");

        var instructions = method.instructions;
        var entry = null;

        for(var index = 0, instrcount = instructions.size(); index < instrcount; index++){
            if (checkInstruction(instructions.get(index), opcodes.INVOKEVIRTUAL, "func_217607_a") &&
                checkOpcodeChain(instructions, index - 5, [ opcodes.ALOAD, opcodes.GETFIELD, opcodes.ILOAD, opcodes.ALOAD ])
            ){
                entry = index;
                break;
            }
        }

        if (entry === null){
            return false;
        }

        print("Found entry point at " + entry + ".");

        var toRemove = instructions.get(entry - 4);
        var toReplace = instructions.get(entry);

        return function(){
            var call = api.buildMethodCall("chylex/bettersprinting/client/player/LivingUpdate", "injectMovementInputUpdate", "(Lnet/minecraft/client/entity/player/ClientPlayerEntity;ZZ)V", api.MethodType.STATIC);

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
                checkInstruction(instructions.get(index - 24), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index - 48), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
                checkInstruction(instructions.get(index - 72), opcodes.INVOKEVIRTUAL, "pushOutOfBlocks", "func_213282_i") &&
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

    var transformAll = function(method){
        var f1 = transformMovementInputUpdate(method);
        if (f1 === false) return false;

        var f2 = transformSprinting(method);
        if (f2 === false) return false;

        var f3 = transformAfterSuperCall(method);
        if (f3 === false) return false;

        f1(); f2(); f3();
        return true;
    };
    
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

                if (!transformAll(methodNode)){
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
