package dev.kaorios.engine.dex

import org.jf.baksmali.Baksmali
import org.jf.baksmali.BaksmaliOptions
import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.Opcodes
import org.jf.smali.Smali
import org.jf.smali.SmaliOptions
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object SmaliRoundTrip {
    fun extractDexContaining(jarPath: String, targetClassDescriptor: String, apiLevel: Int): Pair<String, File>? {
        ZipFile(File(jarPath)).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (!entry.name.endsWith(".dex")) continue
                val tmp = File.createTempFile("probe_", ".dex")
                zip.getInputStream(entry).use { input -> tmp.outputStream().use { input.copyTo(it) } }
                val dexFile = DexFileFactory.loadDexFile(tmp, Opcodes.forApi(apiLevel))
                if (dexFile.classes.any { it.type == targetClassDescriptor }) {
                    return entry.name to tmp
                }
                tmp.delete()
            }
        }
        return null
    }

    fun disassemble(dexFile: File, outDir: File, apiLevel: Int): Boolean {
        val options = BaksmaliOptions().apply {
            this.apiLevel = apiLevel
            jobs = Runtime.getRuntime().availableProcessors()
            parameterRegisters = true
            localsDirective = true
            debugInfo = false
        }
        return try {
            Baksmali.disassembleDexFile(DexFileFactory.loadDexFile(dexFile, Opcodes.forApi(apiLevel)), outDir, options)
            true
        } catch (t: Throwable) { false }
    }

    fun assemble(smaliDir: File, outDex: File, apiLevel: Int): Boolean {
        val options = SmaliOptions().apply {
            this.apiLevel = apiLevel
            verboseErrors = false
        }
        return try {
            Smali.assemble(options, smaliDir.absolutePath, outDex.absolutePath)
            true
        } catch (t: Throwable) { false }
    }

    fun rebuildJar(originalJar: File, patchedDex: File, targetDexEntry: String, outputJar: File): Boolean {
        return try {
            ZipFile(originalJar).use { zip ->
                ZipOutputStream(outputJar.outputStream()).use { zos ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val newEntry = ZipEntry(entry.name)
                        newEntry.time = entry.time
                        if (entry.name == targetDexEntry) {
                            newEntry.method = ZipEntry.DEFLATED
                        } else {
                            newEntry.method = entry.method
                            if (entry.method == ZipEntry.STORED) {
                                newEntry.size = entry.size
                                newEntry.compressedSize = entry.compressedSize
                                newEntry.crc = entry.crc
                            }
                        }
                        zos.putNextEntry(newEntry)
                        if (entry.name == targetDexEntry) {
                            patchedDex.inputStream().use { it.copyTo(zos) }
                        } else {
                            zip.getInputStream(entry).use { it.copyTo(zos) }
                        }
                        zos.closeEntry()
                    }
                }
            }
            true
        } catch (t: Throwable) { false }
    }
}
