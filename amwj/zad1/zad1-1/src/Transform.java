import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;

public class Transform {

    /**
     * orginal class
     */
    JavaClass javaClass;
    /**
     * result of transform
     */
    ClassGen gen;
    /**
     * for easier bytecode creation
     */
    InstructionFactory factory;
    /**
     * constantPool for result of transform
     */
    ConstantPoolGen constantPool;


    /**
     * creates new Transform instance for given class name (for def package)
     *
     * @param className
     * @throws IOException
     */
    public Transform(String className) throws IOException {
        ClassParser parser = new ClassParser(Transform.class.getResourceAsStream(className), className);
        javaClass = parser.parse();
        gen = new ClassGen(javaClass);
        constantPool = gen.getConstantPool();
        factory = new InstructionFactory(gen);
    }


    /**
     * do transform on gen class
     *
     * @return this transform
     */
    public Transform transform() {


        for (Method method : javaClass.getMethods()) {

            InstructionList oldList = new InstructionList(method.getCode().getCode());
            InstructionList newList = oldList.copy();

            for (InstructionHandle i : newList.getInstructionHandles()) {
                if (i.getInstruction() instanceof InvokeInstruction) {
                    newList = packMethodInvocation(newList, (InvokeInstruction) i.getInstruction());
                }

            }
            MethodGen newMethod = new MethodGen(method, javaClass.getClassName(), gen.getConstantPool());

            newMethod.setInstructionList(newList);

            gen.removeMethod(method);

            //finalize the method
            newMethod.stripAttributes(true);
            newMethod.setMaxStack();
            newMethod.setMaxLocals();
            oldList.dispose();
            gen.addMethod(newMethod.getMethod());

            System.out.println(newMethod.getName());
            System.out.println(newMethod.getMethod().getCode());

        }
        return this;
    }

    /**
     * saves result of transform in given directory
     *
     * @param dir place to save
     * @return this transform
     * @throws IOException
     */
    public Transform save(String dir) throws IOException {

        FileOutputStream fos = null;
        try {
            File parent = new File(dir);

            fos = new FileOutputStream(new File(parent, gen.getClassName() + ".class"));
            gen.getJavaClass().dump(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ee) {
                    System.err.println("During closing the stream:");
                    ee.printStackTrace();
                }
            }
        }
        return this;
    }


    /**
     * pack method invocation in "about to call" and "got result"
     *
     * @param list instructionList of method
     * @param i    instruction to 'pack'
     * @return new InstructionList for Method
     */
    public InstructionList packMethodInvocation(InstructionList list, InvokeInstruction i) {

        Type retType = i.getReturnType(constantPool);

        StringBuilder builder = new StringBuilder("About to call: ")
                .append(i.getMethodName(constantPool)).append("(");
        for (Type t : i.getArgumentTypes(constantPool)) {
            builder.append(t.getSignature());
        }
        builder.append(")").append(retType.getSignature());

        list.insert(i, factory.createPrintln(builder.toString()));

        if (i.getOpcode() != Constants.INVOKESPECIAL) {

            InstructionList resultList = new InstructionList();

            String sbClassName = StringBuilder.class.getName();

            Type sbType = Type.getType("Ljava/lang/StringBuilder;");


            //new class
            int builderIndex = constantPool.addClass(sbClassName);
            resultList.append(factory.createNew(sbClassName));

            int resultLength = retType.getSize();
            dup(resultList, 1, resultLength);

            StringBuilder result = new StringBuilder("Got result: ");

            resultList.append(new PUSH(constantPool, result.toString()));
            int sbConstructorIndex = constantPool.addMethodref(sbClassName, "<init>", "(Ljava/lang/String;)V");
            resultList.append(new INVOKESPECIAL(sbConstructorIndex));


            dup(resultList, resultLength, 1);


            //calling append
            if (retType != Type.VOID) {
                String appendRef;
                if (i.getSignature(constantPool).startsWith("L")) {
                    appendRef = Type.getMethodSignature(sbType, new Type[]{Type.getType("Ljava/lang/Object;")});
                } else {
                    appendRef = Type.getMethodSignature(sbType, new Type[]{i.getType(constantPool)});
                }

                int sbAddIndex = constantPool.addMethodref(sbClassName, "append", appendRef);
                resultList.append(new INVOKEVIRTUAL(sbAddIndex));
            }

            //calling println
            int out = constantPool.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
            int println = constantPool.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/Object;)V");
            resultList.append(new GETSTATIC(out));
            resultList.append(new SWAP());
            resultList.append(new INVOKEVIRTUAL(println));
            list.append(i, resultList);
        }

        return list;
    }

    /**
     * duplicate the top instruction on stack and insert its copy after length world (supported 1 and 2 or other no effect)
     *
     * @param list
     */
    public void dup(InstructionList list, int topLength, int bottomLength) {

        if (topLength == 1) {
            if (bottomLength == 1) {
                list.append(new DUP_X1());
            } else if (bottomLength == 2) {
                list.append(new DUP_X2());
            } else if (bottomLength == 0) {
                list.append(new DUP());
            }
        } else if (topLength == 2) {
            if (bottomLength == 1) {
                list.append(new DUP2_X1());
            } else if (bottomLength == 2) {
                list.append(new DUP2_X2());
            }
        }
    }


    public static void main(String[] args) throws IOException {

        new Transform("Test.class").transform().save(".");

    }
}
