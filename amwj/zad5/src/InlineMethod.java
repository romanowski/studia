import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 23.12.12
 * Time: 19:59
 * To change this template use File | Settings | File Templates.
 */
public class InlineMethod {


    InstructionList instructionList;
    int offset;
    LocalVariableTable localVariableTable;
    int newVariablesCount = 0;
    Method m;

    public InlineMethod(Method m, int offset) {
        this.instructionList = new InstructionList(m.getCode().getCode());
        this.offset = offset;
        this.m = m;
        this.localVariableTable = m.getLocalVariableTable();
    }


    public InstructionList convert(MethodGen newMethod, InstructionHandle start, InstructionHandle stop) {
        InstructionList newList = instructionList.copy();


        ConstantPoolGen constantPool = newMethod.getConstantPool();

        ConstantPoolGen mgen = new ConstantPoolGen(m.getConstantPool());


        //fix variables index
        for (InstructionHandle ih : newList.getInstructionHandles()) {
            if ((ih.getInstruction() instanceof LocalVariableInstruction)) {
                LocalVariableInstruction lvi = (LocalVariableInstruction) ih.getInstruction();
                lvi.setIndex(lvi.getIndex() + offset);
            }
            if (ih.getInstruction() instanceof CPInstruction) {
                CPInstruction fom = (CPInstruction) ih.getInstruction();

                Constant c = m.getConstantPool().getConstant(fom.getIndex());
                int newIndex = constantPool.addConstant(c, mgen);

                fom.setIndex(newIndex);
            }
            if (ih.getInstruction() instanceof ReturnInstruction) {
                newList.insert(ih, new GOTO(stop));
                try {
                    newList.delete(ih);
                } catch (TargetLostException e) {
                    InstructionHandle[] targets = e.getTargets();
                    for (int i = 0; i < targets.length; i++) {
                        InstructionTargeter[] targeters = targets[i].getTargeters();

                        for (int j = 0; j < targeters.length; j++)
                            targeters[j].updateTarget(targets[i], ih.getNext());
                    }
                }
            }
        }


        //rewriting localVariables
        for (LocalVariable v : localVariableTable.getLocalVariableTable()) {
            LocalVariable nv = newMethod
                    .addLocalVariable("inline_" + offset, Type.getType(v.getSignature()),
                            offset + v.getIndex(), start, stop).getLocalVariable(newMethod.getConstantPool());
        }

        //fix this   operator
        newList.insert(new ASTORE(offset));
        Type[] types = m.getArgumentTypes();

        //fix function args
        for (int i = types.length - 1; i > 0; i--) {
            Instruction ni = new ASTORE(offset + i);
            if (types[i].equals(BasicType.LONG)) {
                ni = new LSTORE(offset + i);
            }
            if (types[i].equals(BasicType.DOUBLE)) {
                ni = new DSTORE(offset + i);
            }
            if (types[i].equals(BasicType.INT)) {
                ni = new ISTORE(offset + i);
            }
            if (types[i].equals(BasicType.FLOAT)) {
                ni = new FSTORE(offset + i);
            }
            newList.insert(ni);
        }

        newVariablesCount += types.length;


        return newList;
    }
}


