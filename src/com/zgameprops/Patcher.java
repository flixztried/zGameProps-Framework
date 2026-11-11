package com.zgameprops;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.base.reference.BaseTypeReference;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction3rc;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.immutable.ImmutableMethod;
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference;
import org.jf.dexlib2.util.MethodUtil;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.dexlib2.writer.pool.DexPool;
import com.google.common.collect.Iterables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
        if (args.length < 2) {
            System.out.println("Usage: Patcher <input_jar> <output_jar>");
            System.exit(1);
        }
        String inputJarPath = args[0];
        String outputJarPath = args[1];

        try {
            File inputJar = new File(inputJarPath);
            File outputJar = new File(outputJarPath);
            File tempOutDex = new File(outputJar.getParentFile(), "temp_patched.dex");

            // 1. Load the entire DexContainer (handles multi-dex)
            org.jf.dexlib2.iface.MultiDexContainer<? extends DexFile> container = 
                DexFileFactory.loadDexContainer(inputJar, Opcodes.forApi(34));

            String targetDexEntry = null;
            DexFile targetDexFile = null;

            // 2. Find which dex file contains Instrumentation
            for (String entryName : container.getDexEntryNames()) {
                org.jf.dexlib2.iface.MultiDexContainer.DexEntry<? extends DexFile> entry = container.getEntry(entryName);
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

            if (targetDexEntry == null) {
                System.out.println("Instrumentation class not found!");
                System.exit(1);
            }

            // 3. Re-pool the modified DEX
            DexPool dexPool = new DexPool(Opcodes.forApi(34));
            for (ClassDef classDef : targetDexFile.getClasses()) {
                if (classDef.getType().equals("Landroid/app/Instrumentation;")) {
                    dexPool.internClass(new PatchedClass(classDef));
                } else {
                    dexPool.internClass(classDef);
                }
            }

            // Inject GamesPropsUtils from our own JAR
            String currentClassPath = System.getProperty("java.class.path");
            File currentJar = new File(currentClassPath);
            org.jf.dexlib2.iface.MultiDexContainer<? extends DexFile> utilsContainer = 
                DexFileFactory.loadDexContainer(currentJar, Opcodes.forApi(34));
            
            for (String entryName : utilsContainer.getDexEntryNames()) {
                org.jf.dexlib2.iface.MultiDexContainer.DexEntry<? extends DexFile> entry = utilsContainer.getEntry(entryName);
                for (ClassDef classDef : entry.getDexFile().getClasses()) {
                    if (classDef.getType().startsWith("Lio/mesalabs/unica/GamesPropsUtils")) {
                        dexPool.internClass(classDef);
                    }
                }
            }

            FileDataStore outStore = new FileDataStore(tempOutDex);
            dexPool.writeTo(outStore);

            // 4. Rebuild the JAR: Copy all original entries, replacing only the target DEX
            ZipFile zipFile = new ZipFile(inputJar);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                zos.putNextEntry(new ZipEntry(entry.getName()));
                
                if (entry.getName().equals(targetDexEntry)) {
                    // Write our patched DEX
                    java.nio.file.Files.copy(tempOutDex.toPath(), zos);
                } else {
                    // Copy original entry byte-for-byte
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

    // Extending BaseTypeReference avoids boilerplate
    static class PatchedClass extends BaseTypeReference implements ClassDef {
        private final ClassDef base;

        PatchedClass(ClassDef base) { this.base = base; }

        @Override public String getType() { return base.getType(); }
        @Override public int getAccessFlags() { return base.getAccessFlags(); }
        @Override public String getSuperclass() { return base.getSuperclass(); }
        @Override public List<String> getInterfaces() { return base.getInterfaces(); }
        @Override public String getSourceFile() { return base.getSourceFile(); }
        @Override public Set<? extends org.jf.dexlib2.iface.Annotation> getAnnotations() { return base.getAnnotations(); }
        
        @Override public Iterable<? extends Field> getFields() {
            return Iterables.concat(getStaticFields(), getInstanceFields());
        }

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
        
        int ctxIndex = -1;
        List<? extends CharSequence> paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.size(); i++) {
            if (paramTypes.get(i).toString().equals("Landroid/content/Context;")) {
                ctxIndex = i;
                break;
            }
        }

        if (ctxIndex == -1) return method;

        boolean isStatic = AccessFlags.STATIC.isSet(method.getAccessFlags());
        int paramRegCount = MethodUtil.getParameterRegisterCount(method);
        
        // Calculate absolute register dynamically
        int reg = mi.getRegisterCount() - paramRegCount + (isStatic ? 0 : 1) + ctxIndex;

        ImmutableMethodReference setProps = new ImmutableMethodReference(
            "Lio/mesalabs/unica/GamesPropsUtils;", "setProps",
            Collections.singletonList("Landroid/content/Context;"), "V"
        );

        // Inject at index 0
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
