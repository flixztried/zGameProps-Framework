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

            // Find the specific DEX containing Instrumentation
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

            if (targetDexEntry == null) {
                throw new RuntimeException("Instrumentation class not found in framework.jar!");
            }
            System.out.println("Found Instrumentation in: " + targetDexEntry);

            int originalClassCount = 0;
            DexPool dexPool = new DexPool(Opcodes.forApi(apiLevel));
            
            // Only re-pool the target DEX
            for (ClassDef classDef : targetDexFile.getClasses()) {
                originalClassCount++;
                if (classDef.getType().equals("Landroid/app/Instrumentation;")) {
                    dexPool.internClass(new PatchedClass(classDef));
                } else {
                    dexPool.internClass(classDef);
                }
            }

            // Inject GamesPropsUtils
            String currentClassPath = System.getProperty("java.class.path");
            File currentJar = new File(currentClassPath);
            com.android.tools.smali.dexlib2.iface.MultiDexContainer<? extends DexFile> utilsContainer = 
                DexFileFactory.loadDexContainer(currentJar, Opcodes.forApi(apiLevel));
            
            int injectedCount = 0;
            for (String entryName : utilsContainer.getDexEntryNames()) {
                com.android.tools.smali.dexlib2.iface.MultiDexContainer.DexEntry<? extends DexFile> entry = utilsContainer.getEntry(entryName);
                for (ClassDef classDef : entry.getDexFile().getClasses()) {
                    if (classDef.getType().startsWith("Lio/mesalabs/unica/GamesPropsUtils")) {
                        dexPool.internClass(classDef);
                        injectedCount++;
                    }
                }
            }

            FileDataStore outStore = new FileDataStore(tempOutDex);
            dexPool.writeTo(outStore);

            // VALIDATION: Re-open the patched DEX and check class count
            com.android.tools.smali.dexlib2.iface.DexFile patchedFile = 
                DexFileFactory.loadDexFile(tempOutDex, Opcodes.forApi(apiLevel));
            int finalClassCount = 0;
            for (ClassDef ignored : patchedFile.getClasses()) {
                finalClassCount++;
            }

            int expectedCount = originalClassCount + injectedCount;
            if (finalClassCount != expectedCount) {
                throw new RuntimeException("Validation failed! Expected " + expectedCount + " classes, but got " + finalClassCount);
            }
            System.out.println("Validation passed: " + finalClassCount + " classes present.");

            // Rebuild the JAR
            ZipFile zipFile = new ZipFile(inputJar);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                zos.putNextEntry(new ZipEntry(entry.getName()));
                
                if (entry.getName().equals(targetDexEntry)) {
                    java.nio.file.Files.copy(tempOutDex.toPath(), zos);
                } else {
                    // Copy other DEX files and resources byte for byte
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
        int reg = mi.getRegisterCount() - paramRegCount + (isStatic ? 0 : 1) + ctxIndex;

        ImmutableMethodReference setProps = new ImmutableMethodReference(
            "Lio/mesalabs/unica/GamesPropsUtils;", "setProps",
            Collections.singletonList("Landroid/content/Context;"), "V"
        );

        if (reg <= 15) {
            mi.addInstruction(0, new BuilderInstruction35c(Opcode.INVOKE_STATIC, 1, reg, 0, 0, 0, 0, setProps));
        } else {
            mi.addInstruction(0, new BuilderInstruction3rc(Opcode.INVOKE_STATIC_RANGE, reg, 1, setProps));
        }

        // We use Collections.emptySet() for restrictions to avoid any lingering parsing bugs
        return new ImmutableMethod(
            method.getDefiningClass(), method.getName(), method.getParameters(),
            method.getReturnType(), method.getAccessFlags(),
            method.getAnnotations(), Collections.emptySet(), mi
        );
    }
}
