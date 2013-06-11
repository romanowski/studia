import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ProblemSixSolver {

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


    Map<String, Method> rewritingMethod = new HashMap<String, Method>();
    String secClass;


    /**
     * creates new ProblemSixSolver instance for given class name (for def package)
     *
     * @param className
     * @throws IOException
     */
    public ProblemSixSolver(String className) throws IOException {
        ClassParser parser = new ClassParser(ProblemSixSolver.class.getResourceAsStream(className), className);
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
    public ProblemSixSolver transform() {
        try {

            for (Method method : javaClass.getMethods()) {
                InstructionList oldList = new InstructionList(method.getCode().getCode());
                InstructionList newList = oldList.copy();
                if (method.getLocalVariableTable() == null) {
                    continue;
                }
                int nextFreeIndex = method.getLocalVariableTable().getLength();

                MethodGen newMethod = new MethodGen(method, javaClass.getClassName(), gen.getConstantPool());

                boolean found = true;
                while (found) {
                    found = false;
                    Map<InstructionHandle, InstructionList> toDelete = new HashMap<InstructionHandle, InstructionList>();

                    for (InstructionHandle i : newList.getInstructionHandles()) {
                        if (i.getInstruction() instanceof INVOKEVIRTUAL) {
                            INVOKEVIRTUAL iv = (INVOKEVIRTUAL) i.getInstruction();

                            String objRef = iv.getReferenceType(constantPool).getSignature();
                            if (objRef.equals(secClass)) {
                                found = true;
                                Method revMethod = rewritingMethod.get(iv.getMethodName(constantPool) + "#" + iv.getSignature(constantPool));
                                InlineMethod ni = new InlineMethod(revMethod, nextFreeIndex);
                                toDelete.put(i, ni.convert(newMethod, i, i.getNext()));
                                nextFreeIndex += ni.newVariablesCount;
                            }
                        }
                    }
                    for (InstructionHandle ih : toDelete.keySet()) {
                        newList.append(ih.getPrev(), toDelete.get(ih));
                        // newList.delete(ih.getInstruction());
                    }

                    for (InstructionHandle ih : toDelete.keySet()) {
                        try {
                            newList.delete(ih.getInstruction());
                        } catch (TargetLostException e) {
                            InstructionHandle[] targets = e.getTargets();
                            for (int i = 0; i < targets.length; i++) {
                                InstructionTargeter[] targeters = targets[i].getTargeters();

                                for (int j = 0; j < targeters.length; j++)
                                    targeters[j].updateTarget(targets[i], toDelete.get(ih).getStart());
                            }
                        }
                    }
                }


                newMethod.setInstructionList(newList);

                gen.removeMethod(method);


                //finalize the method
                newMethod.stripAttributes(true);
                newMethod.setMaxStack();
                newMethod.setMaxLocals();
                oldList.dispose();
                gen.addMethod(newMethod.getMethod());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return this;
    }

    public ProblemSixSolver load(String className) throws Exception {

        ClassParser parser = new ClassParser(className);
        JavaClass inlineClass = parser.parse();

        for (Method m : inlineClass.getMethods()) {
            rewritingMethod.put(m.getName() + "#" + m.getSignature(), m);
        }
        secClass = Type.getType(Class.forName(className.replace(".class", ""))).getSignature();

        return this;
    }


    /**
     * saves result of transform in given directory
     *
     * @param dir place to save
     * @return this transform
     * @throws IOException
     */
    public ProblemSixSolver save(String dir) throws IOException {

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


    public static void main(String[] args) throws Exception {

        new ProblemSixSolver(args[0] + ".class").load(args[1] + ".class").transform().save(".");

    }
}