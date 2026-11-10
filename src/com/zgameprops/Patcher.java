package com.zgameprops;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.immutable.ImmutableMethod;
import org.jf.dexlib2.immutable.ImmutableMethodImplementation;
import org.jf.dexlib2.immutable.instruction.ImmutableInstruction35c;
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference;
import org.jf.dexlib2.writer.pool.DexPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
                    List<Method> newMethods = new ArrayList<>();
                    for (Method method : classDef.getMethods()) {
                        if (method.getName().equals("newApplication")) {
                            newMethods.add(createPatchedMethod(method));
                        } else {
                            newMethods.add(method);
                        }
                    }
                    dexPool.internClass(org.jf.dexlib2.immutable.ImmutableClassDef.of(classDef, newMethods));
                } else {
                    dexPool.internClass(classDef);
                }
            }
            
            // Inject GamesPropsUtils class
            org.jf.dexlib2.iface.MultiDexContainer.DexEntry utilsEntry = 
                DexFileFactory.loadDexEntry(new File("classes.dex"), true, Opcodes.forApi(34));
            for (ClassDef classDef : utilsEntry.getDexFile().getClasses()) {
                dexPool.internClass(classDef);
            }

            File outDex = new File("patched.dex");
            FileOutputStream fos = new FileOutputStream(outDex);
            dexPool.writeTo(fos);
            fos.close();
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
        
        // Invoke static
        newInstructions.add(new ImmutableInstruction35c(0x71, 1, reg, 0, 0, 0, 0, setProps));
        
        // Add return
        newInstructions.add(insts.get(insts.size() - 1));

        // FIX: Pass the annotations and hiddenApiRestrictions from the original method
        return new ImmutableMethod(
            method.getDefiningClass(), method.getName(), method.getParameters(),
            method.getReturnType(), method.getAccessFlags(),
            method.getAnnotations(), method.getHiddenApiRestrictions(),
            new ImmutableMethodImplementation(method.getImplementation().getRegisterCount(),
                newInstructions, method.getImplementation().getTryBlocks(),
                method.getImplementation().getDebugItems())
        );
    }
}
