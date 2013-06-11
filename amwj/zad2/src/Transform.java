import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

            int counterMethod = constantPool.addMethodref(Counter.class.getCanonicalName(), Counter.putName, Counter.putSignature);

            for (InstructionHandle i : newList.getInstructionHandles()) {
                if (packInstruction(i.getInstruction())) {
                    InstructionList statistic = new InstructionList();
                    statistic.append(factory.createConstant(i.getInstruction().getName()));
                    statistic.append(new INVOKESTATIC(counterMethod));
                    newList.insert(i, statistic);
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

        }
        return this;
    }

    private boolean packInstruction(Instruction ins) {
        if (ins instanceof InvokeInstruction) {
            String signature = ((InvokeInstruction) ins).getReferenceType(constantPool).getSignature();
            String thisSignature = "L" + javaClass.getClassName() + ";";
            boolean result = signature.equals(thisSignature);
            return result;
        }
        return true;
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

            fos.close();

            //copy main class
            fos = new FileOutputStream(new File(parent, Counter.class.getName() + ".class"));
            InputStream is = Counter.class.getResourceAsStream(Counter.class.getName() + ".class");
            byte[] tmp = new byte[1024];
            int read = 1;
            while ((read = is.read(tmp)) > -1) {
                fos.write(tmp, 0, read);
            }
            is.close();

            //copy runnable
            fos = new FileOutputStream(new File(parent, Counter.class.getName() + "$1.class"));
            is = Counter.class.getResourceAsStream(Counter.class.getName() + "$1.class");
            read = 0;
            while ((read = is.read(tmp)) > -1) {
                fos.write(tmp, 0, read);
            }
            is.close();
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


    public static void main(String[] args) throws IOException {

        System.out.println(args[0] + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        new Transform(args[0] + ".class").transform().save(".");

    }
}