#!/system/bin/sh
SKIPUNZIP=0

LOG_FILE="/sdcard/Documents/zGameProps_install_log.txt"
FALLBACK_LOG="/data/local/tmp/zGameProps_install_log.txt"

echo "--- zGameProps Installation Log ---" > "$LOG_FILE" 2>/dev/null || echo "" > "$FALLBACK_LOG"
echo "Date: $(date)" >> "$LOG_FILE" 2>/dev/null || echo "Date: $(date)" >> "$FALLBACK_LOG"
echo "Patcher Version: 1.0.1 (dexlib2 3.0.9)" >> "$LOG_FILE" 2>/dev/null || echo "Patcher Version: 1.0.1 (dexlib2 3.0.9)" >> "$FALLBACK_LOG"

log_msg() {
    ui_print "$1"
    echo "$1" >> "$LOG_FILE" 2>/dev/null || echo "$1" >> "$FALLBACK_LOG"
}

FRAMEWORK_JAR="/system/framework/framework.jar"
BACKUP_DIR="/data/adb/zgameprops_framework_backup"
BACKUP_JAR="$BACKUP_DIR/framework.jar"
WORK_JAR="/data/local/tmp/framework_work.jar"
WORK_JAR_OUT="/data/local/tmp/framework_patched.jar"
PATCHER_JAR="/data/local/tmp/patcher.jar"
OUT_DIR="$MODPATH/system/framework"

API_LEVEL=$(getprop ro.build.version.sdk)
log_msg "- zGameProps Framework Patcher"
log_msg "- Device API Level: $API_LEVEL"

if [ ! -f "$MODPATH/patcher.jar" ]; then
    log_msg "! patcher.jar missing."
    abort "! patcher.jar missing."
fi

# Detect if old module is still active
if [ -d "/data/adb/modules/zGameProps-Framework" ] && [ ! -f "/data/adb/modules/zGameProps-Framework/disable" ]; then
    log_msg "! WARNING: Previous zGameProps-Framework detected."
    log_msg "! The current framework.jar is likely ALREADY PATCHED."
    log_msg "! Recommend: uninstall old module, reboot, then reflash."
fi

# Diagnostics: framework.jar structure
log_msg "- Inspecting stock framework.jar..."
unzip -l "$FRAMEWORK_JAR" 2>/dev/null | grep -E '\.dex' >> "$LOG_FILE" 2>/dev/null || \
    unzip -l "$FRAMEWORK_JAR" 2>/dev/null | grep -E '\.dex' >> "$FALLBACK_LOG" 2>/dev/null

# Determine source jar (prefer backup if it exists and differs from current)
PATCH_SOURCE="$FRAMEWORK_JAR"
mkdir -p "$BACKUP_DIR"

if [ -f "$BACKUP_JAR" ]; then
    log_msg "- Backup found. Comparing hashes..."
    SRC_HASH=$(sha256sum "$FRAMEWORK_JAR" 2>/dev/null | cut -d' ' -f1)
    BAK_HASH=$(sha256sum "$BACKUP_JAR" 2>/dev/null | cut -d' ' -f1)
    log_msg "  Current: $SRC_HASH"
    log_msg "  Backup:  $BAK_HASH"
    
    if [ "$SRC_HASH" != "$BAK_HASH" ]; then
        log_msg "- Current framework.jar differs from backup (overlay active)."
        log_msg "- Using BACKUP as patch source (guaranteed stock)."
        PATCH_SOURCE="$BACKUP_JAR"
    else
        log_msg "- Backup matches current. Using current as source."
    fi
else
    log_msg "- Creating initial backup from current framework.jar."
    cp "$FRAMEWORK_JAR" "$BACKUP_JAR"
fi

# Sanity check the source
SRC_SIZE=$(ls -l "$PATCH_SOURCE" | awk '{print $5}')
log_msg "- Patch source: $PATCH_SOURCE ($SRC_SIZE bytes)"

if [ "$SRC_SIZE" -lt 1000000 ]; then
    log_msg "! Source jar suspiciously small. Aborting."
    abort "! Source jar size invalid."
fi

log_msg "- Preparing environment..."
mkdir -p "$OUT_DIR"
cp "$MODPATH/patcher.jar" "$PATCHER_JAR"
cp "$PATCH_SOURCE" "$WORK_JAR"
chmod 777 "$PATCHER_JAR" "$WORK_JAR"

log_msg "- Starting app_process..."
export CLASSPATH="$PATCHER_JAR"

app_process -Xmx1024m /system/bin com.zgameprops.Patcher "$WORK_JAR" "$WORK_JAR_OUT" "$API_LEVEL" >> "$LOG_FILE" 2>&1
PATCH_RESULT=$?

if [ $PATCH_RESULT -ne 0 ]; then
    log_msg "! Patching failed with exit code $PATCH_RESULT."
    log_msg "- Check $LOG_FILE for full stack trace."
    rm -f "$PATCHER_JAR" "$WORK_JAR" "$WORK_JAR_OUT"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful!"
log_msg "- First boot will be slow (ART cache rebuild)."
mv "$WORK_JAR_OUT" "$OUT_DIR/framework.jar"
rm -f "$PATCHER_JAR" "$WORK_JAR"
