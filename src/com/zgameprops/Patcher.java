package com.zgameprops;

import com.android.tools.smali.dexlib2.AccessFlags;
import com.android.tools.smali.dexlib2.DexFileFactory;
import com.android.tools.smali.dexlib2.Opcode;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.base.reference.BaseTypeReference;
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation;
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c;
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction3rc;
import com.android.tools.smali.dexlib2.iface.ClassDef;
import com.android.tools.smali.dexlib2.iface.DexFile;
import com.android.tools.smali.dexlib2.iface.Field;
import com.android.tools.smali.dexlib2.iface.Method;
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod;
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference;
import com.android.tools.smali.dexlib2.util.MethodUtil;
import com.android.tools.smali.dexlib2.writer.io.FileDataStore;
import com.android.tools.smali.dexlib2.writer.pool.DexPool;
import com.google.common.collect.Iterables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Patcher {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: Patcher <input_jar> <output_jar> <api_level>");
            System.exit(1);
        }
        String inputJarPath = args[0];
        String outputJarPath = args[1];
        int apiLevel = Integer.parseInt(args[2]);

        try {
            System.out.println("Using API level: " + apiLevel);
            File inputJar = new File(inputJarPath);
            File outputJar = new File(outputJarPath);
            File tempOutDex = new File(outputJar.getParentFile(), "temp_patched.dex");

            com.android.tools.smali.dexlib2.iface.MultiDexContainer<? extends DexFile> container = 
                DexFileFactory.loadDexContainer(inputJar, Opcodes.forApi(apiLevel));

            String targetDexEntry = null;
            DexFile targetDexFile = null;

            for (String entryName : container.getDexEntryNames()) {
                com.android.tools.smali.dexlib2.iface.MultiDexContainer.DexEntry<? extends DexFile> entry = container.getEntry(entryName);
                if (entry != null) {
                    for (ClassDef classDef : entry.getDexFile().getClasses()) {
                        if (classDef.getType().equals("Landroid/app/Instrumentation;")) {
                            targetDexEntry = entryName;
                            targetDexFile = entry.getDexFile();
                            break;
                        }
                    }
                }
                if (targetDexEntry != null) break;
            }

            if (targetDexEntry == null) throw new RuntimeException("Instrumentation class not found!");

            DexPool dexPool = new DexPool(Opcodes.forApi(apiLevel));
            for (ClassDef classDef : targetDexFile.getClasses()) {
                if (classDef.getType().equals("Landroid/app/Instrumentation;")) {
                    dexPool.internClass(new PatchedClass(classDef));
                } else {
                    dexPool.internClass(classDef);
                }
            }

            String currentClassPath = System.getProperty("java.class.path");
            File currentJar = new File(currentClassPath);
            com.android.tools.smali.dexlib2.iface.MultiDexContainer<? extends DexFile> utilsContainer = 
                DexFileFactory.loadDexContainer(currentJar, Opcodes.forApi(apiLevel));
            
            for (String entryName : utilsContainer.getDexEntryNames()) {
                com.android.tools.smali.dexlib2.iface.MultiDexContainer.DexEntry<? extends DexFile> entry = utilsContainer.getEntry(entryName);
                for (ClassDef classDef : entry.getDexFile().getClasses()) {
                    if (classDef.getType().startsWith("Lio/mesalabs/unica/GamesPropsUtils")) {
                        dexPool.internClass(classDef);
                    }
                }
            }

            FileDataStore outStore = new FileDataStore(tempOutDex);
            dexPool.writeTo(outStore);

            ZipFile zipFile = new ZipFile(inputJar);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                zos.putNextEntry(new ZipEntry(entry.getName()));
                
                if (entry.getName().equals(targetDexEntry)) {
                    java.nio.file.Files.copy(tempOutDex.toPath(), zos);
                } else {
                    InputStream is = zipFile.getInputStream(entry);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    is.close();
                }
                zos.closeEntry();
            }
            zos.close();
            zipFile.close();
            tempOutDex.delete();
            System.out.println("Patch successful.");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static class PatchedClass extends BaseTypeReference implements ClassDef {
        private final ClassDef base;
        PatchedClass(ClassDef base) { this.base = base; }
        @Override public String getType() { return base.getType(); }
        @Override public int getAccessFlags() { return base.getAccessFlags(); }
        @Override public String getSuperclass() { return base.getSuperclass(); }
        @Override public List<String> getInterfaces() { return base.getInterfaces(); }
        @Override public String getSourceFile() { return base.getSourceFile(); }
        @Override public Set<? extends com.android.tools.smali.dexlib2.iface.Annotation> getAnnotations() { return base.getAnnotations(); }
        @Override public Iterable<? extends Field> getFields() { return Iterables.concat(getStaticFields(), getInstanceFields()); }
        @Override public Iterable<? extends Field> getStaticFields() { return base.getStaticFields(); }
        @Override public Iterable<? extends Field> getInstanceFields() { return base.getInstanceFields(); }
        @Override public Iterable<? extends Method> getDirectMethods() { return base.getDirectMethods(); }
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
        MutableMethodImplementation mi = new MutableMethodImplementation(method.getImplementation());
        
        // FIX: Context is ALWAYS the last parameter in newApplication.
        // Register count minus 1 gives us the absolute register index for Context.
        int reg = mi.getRegisterCount() - 1;

        System.out.println("Injecting into " + method.getName() + " at register v" + reg);

        ImmutableMethodReference setProps = new ImmutableMethodReference(
            "Lio/mesalabs/unica/GamesPropsUtils;", "setProps",
            Collections.singletonList("Landroid/content/Context;"), "V"
        );

        if (reg <= 15) {
            mi.addInstruction(0, new BuilderInstruction35c(Opcode.INVOKE_STATIC, 1, reg, 0, 0, 0, 0, setProps));
        } else {
            mi.addInstruction(0, new BuilderInstruction3rc(Opcode.INVOKE_STATIC_RANGE, reg, 1, setProps));
        }

        return new ImmutableMethod(
            method.getDefiningClass(), method.getName(), method.getParameters(),
            method.getReturnType(), method.getAccessFlags(),
            method.getAnnotations(), Collections.emptySet(), mi
        );
    }
}
