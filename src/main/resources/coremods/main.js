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
        for(var index = 0; index < instructions.size(); index++){
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
                        opcodeName += ", " + instruction.name;
                    }
                }catch(e){}
                
                try{
                    var desc = instruction.desc;
                    
                    if (desc){
                        opcodeName += ", " + instruction.desc;
                    }
                }catch(e){}
            }
            
            print(indexStr + typeName + opcodeName);
        }
    };
    
    var checkInstructionName = function(instruction, name1, name2){
        return instruction.name.equals(name1) || instruction.name.equals(name2);
    };
    
    var checkOpcodeChain = function(instructions, start, chain){
        for(var offset = 0; offset < chain.length; offset++){
            var instruction = instructions.get(start + offset);
            
            if (instruction.getOpcode() != chain[offset]){
                print("Mismatched opcode chain, " + instruction.getOpcode() + " != " + chain[offset]);
                return false;
            }
        }
        
        return true;
    };
    
    var transformLivingTick = function(method){
        var instructions = method.instructions;
        var instrcount = instructions.size();
        
        var insertionPoint = -1;
        var skipPoint = -1;
        
        for(var index = 0; index < instrcount; index++){
            var instruction = instructions.get(index);
            
            if (instruction.getOpcode() == opcodes.GETFIELD &&
                checkInstructionName(instruction, "movementInput", "field_71158_b") &&
                checkOpcodeChain(instructions, index - 1, [ opcodes.ALOAD, opcodes.GETFIELD, opcodes.GETFIELD, opcodes.ISTORE ]) &&
                checkOpcodeChain(instructions, index + 5, [ opcodes.ALOAD, opcodes.GETFIELD, opcodes.GETFIELD, opcodes.ISTORE ])
            ){
                insertionPoint = index + 9;
                break;
            }
        }
        
        if (insertionPoint == -1){
            return false;
        }
        
        print("Found insertion point at index " + insertionPoint + ".");
        
        for(var index = insertionPoint; index < instrcount; index++){
            var instruction = instructions.get(index);
            
            if (instruction.getOpcode() == opcodes.GETSTATIC &&
                instruction.name.equals("CHEST") &&
                checkOpcodeChain(instructions, index - 1, [ opcodes.ALOAD, opcodes.GETSTATIC, opcodes.INVOKEVIRTUAL, opcodes.ASTORE ]) &&
                checkOpcodeChain(instructions, index - 24, [ opcodes.ALOAD, opcodes.GETFIELD, opcodes.GETFIELD, opcodes.IFEQ ])
            ){
                skipPoint = index - 27;
                break;
            }
        }
        
        if (skipPoint == -1){
            return false;
        }
        
        print("Found skip point at index " + skipPoint + ".");
        
        var insertionPointLabel = instructions.get(insertionPoint);
        var skipPointLabel = instructions.get(skipPoint);
        
        if (insertionPointLabel.getType() != 8){
            print("Insertion point is not a label!");
            return false;
        }
        
        if (skipPointLabel.getType() != 8){
            print("Skip point is not a label!");
            return false;
        }
        
        var skipPointLabelInst = skipPointLabel.getLabel();
        skipPointLabelInst.info = skipPointLabel;
        
        var helper = api.getMethodNode();
        helper.visitVarInsn(opcodes.ALOAD, 0);
        helper.visitMethodInsn(opcodes.INVOKESTATIC, "chylex/bettersprinting/client/player/LivingUpdate", "injectOnLivingUpdate", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false);
        helper.visitJumpInsn(opcodes.GOTO, skipPointLabelInst);
        
        instructions.insert(insertionPointLabel, helper.instructions);
        return true;
    };
    
    var transformPushOutOfBlocks = function(method){
        method.access &= ~opcodes.ACC_PROTECTED;
        method.access |= opcodes.ACC_PUBLIC;
    };
    
    var matchMethod = function(name1, name2, desc){
        return function(method){
            return (method.name.equals(name1) || method.name.equals(name2)) && method.desc.equals(desc);
        };
    };
    
    return {
        "BetterSprintingCore": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.entity.EntityPlayerSP"
            },
            "transformer": function(classNode){
                print("Setting up BetterSprintingCore...");
                
                var livingTick = classNode.methods
                                          .stream()
                                          .filter(matchMethod("livingTick", "func_70636_d", "()V"))
                                          .toArray();
                
                var pushOutOfBlocks = classNode.methods
                                               .stream()
                                               .filter(matchMethod("pushOutOfBlocks", "func_145771_j", "(DDD)Z"))
                                               .toArray();
                
                if (livingTick.length == 1 && pushOutOfBlocks.length == 1){
                    var mLivingTick = livingTick[0];
                    
                    if (transformLivingTick(mLivingTick)){
                        print("Transformed EntityPlayerSP.livingTick().");
                    }
                    else{
                        print("Could not inject into EntityPlayerSP.livingTick(), printing all instructions...");
                        printInstructions(mLivingTick.instructions);
                    }
                    
                    transformPushOutOfBlocks(pushOutOfBlocks[0]);
                    print("Transformed EntityPlayerSP.pushOutOfBlocks().");
                }
                else{
                    print("Could not find EntityPlayerSP.livingTick() and/or EntityPlayerSP.pushOutOfBlocks(), printing all methods...");
                    
                    classNode.methods.forEach(function(method){
                        print(method.name + method.desc);
                    });
                }
                
                print("Finished BetterSprintingCore.");
                return classNode;
            }
        }
    };
}
