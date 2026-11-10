package com.zgameprops;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.immutable.ImmutableMethod;
import org.jf.dexlib2.immutable.ImmutableMethodImplementation;
import org.jf.dexlib2.immutable.instruction.ImmutableInstruction35c;
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.dexlib2.writer.pool.DexPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

public class Patcher {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: Patcher <input_jar> <output_jar>");
            System.exit(1);
        }
        String inputJarPath = args[0];
        String outputJarPath = args[1];

        try {
            File inputJar = new File(inputJarPath);
            ZipFile zipFile = new ZipFile(inputJar);
            
            DexPool dexPool = new DexPool(Opcodes.forApi(34));
            org.jf.dexlib2.iface.MultiDexContainer.DexEntry dexEntryObj = 
                DexFileFactory.loadDexEntry(inputJar, "classes.dex", true, Opcodes.forApi(34));

            for (ClassDef classDef : dexEntryObj.getDexFile().getClasses()) {
                if (classDef.getType().equals("Landroid/app/Instrumentation;")) {
                    dexPool.internClass(new PatchedClass(classDef));
                } else {
                    dexPool.internClass(classDef);
                }
            }
            
            // Inject GamesPropsUtils class
            org.jf.dexlib2.iface.DexFile utilsFile = 
                DexFileFactory.loadDexFile(new File("classes.dex"), Opcodes.forApi(34));
            for (ClassDef classDef : utilsFile.getClasses()) {
                dexPool.internClass(classDef);
            }

            File outDex = new File("patched.dex");
            FileDataStore outStore = new FileDataStore(outDex);
            dexPool.writeTo(outStore);
            zipFile.close();

            // Rebuild Jar
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJarPath));
            zos.putNextEntry(new ZipEntry("classes.dex"));
            java.nio.file.Files.copy(outDex.toPath(), zos);
            zos.closeEntry();
            zos.close();

            System.out.println("Patch successful.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static class PatchedClass implements ClassDef {
        private final ClassDef base;

        PatchedClass(ClassDef base) { this.base = base; }

        @Override public String getType() { return base.getType(); }
        @Override public int getAccessFlags() { return base.getAccessFlags(); }
        @Override public String getSuperclass() { return base.getSuperclass(); }
        @Override public List<String> getInterfaces() { return base.getInterfaces(); }
        @Override public String getSourceFile() { return base.getSourceFile(); }
        @Override public Set<? extends Annotation> getAnnotations() { return base.getAnnotations(); }
        @Override public Iterable<? extends Field> getStaticFields() { return base.getStaticFields(); }
        @Override public Iterable<? extends Field> getInstanceFields() { return base.getInstanceFields(); }
        
        @Override
        public Iterable<? extends Method> getDirectMethods() { return base.getDirectMethods(); }

        @Override
        public Iterable<? extends Method> getVirtualMethods() {
            List<Method> newMethods = new ArrayList<>();
            for (Method m : base.getVirtualMethods()) {
                if (m.getName().equals("newApplication")) {
                    newMethods.add(createPatchedMethod(m));
                } else {
                    newMethods.add(m);
                }
            }
            return newMethods;
        }

        @Override
        public Iterable<? extends Method> getMethods() {
            List<Method> newMethods = new ArrayList<>();
            for (Method m : base.getMethods()) {
                if (m.getName().equals("newApplication")) {
                    newMethods.add(createPatchedMethod(m));
                } else {
                    newMethods.add(m);
                }
            }
            return newMethods;
        }
    }

    private static Method createPatchedMethod(Method method) {
        if (method.getImplementation() == null) return method;
        List<Instruction> newInstructions = new ArrayList<>();
        
        List<Instruction> insts = new ArrayList<>();
        for (Instruction i : method.getImplementation().getInstructions()) {
            insts.add(i);
        }
        
        int reg = method.getParameterTypes().size() > 1 ? 3 : 1;
        if (method.getParameterTypes().size() == 2) reg = 1;
        else reg = 3;

        ImmutableMethodReference setProps = new ImmutableMethodReference(
            "Lio/mesalabs/unica/GamesPropsUtils;", "setProps",
            java.util.Collections.singletonList("Landroid/content/Context;"), "V"
        );

        for (int i = 0; i < insts.size() - 1; i++) {
            newInstructions.add(insts.get(i));
        }
        
        newInstructions.add(new ImmutableInstruction35c(Opcode.INVOKE_STATIC, 1, reg, 0, 0, 0, 0, setProps));
        newInstructions.add(insts.get(insts.size() - 1));

        // Use empty collections to bypass the dexlib2 hidden API parsing crash
        return new ImmutableMethod(
            method.getDefiningClass(), method.getName(), method.getParameters(),
            method.getReturnType(), method.getAccessFlags(),
            Collections.emptySet(), 
            Collections.emptySet(), 
            new ImmutableMethodImplementation(method.getImplementation().getRegisterCount(),
                newInstructions, method.getImplementation().getTryBlocks(),
                method.getImplementation().getDebugItems())
        );
    }
}
