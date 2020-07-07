var checkInstruction = function(instruction, opcode, name1, name2){
    return instruction.getOpcode() === opcode && ((!name1 || instruction.name.equals(name1)) || (name2 && instruction.name.equals(name2)));
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

var transformAll = function(method, transformers){
    var api = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var opcodes = Java.type("org.objectweb.asm.Opcodes");

    var checked = [];

    for(var index = 0; index < transformers.length; index++){
        var func = transformers[index](api, opcodes, method);

        if (func === false){
            return false;
        }

        checked.push(func);
    }

    for(var index = 0; index < checked.length; index++){
        checked[index]();
    }

    return true;
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
