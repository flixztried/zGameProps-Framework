#!/system/bin/sh
SKIPUNZIP=0

LOG_FILE="/sdcard/Documents/zGameProps_install_log.txt"
FALLBACK_LOG="/data/local/tmp/zGameProps_install_log.txt"

echo "--- zGameProps Installation Log ---" > "$LOG_FILE" 2>/dev/null || echo "" > "$FALLBACK_LOG"
echo "Date: $(date)" >> "$LOG_FILE" 2>/dev/null || echo "Date: $(date)" >> "$FALLBACK_LOG"

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

# --- Hybrid Mount Blacklist ---
HM_CONFIG="/data/adb/hybrid-mount/config.toml"
if [ -f "$HM_CONFIG" ]; then
    log_msg "- Blacklisting zGameProps-Framework from Hybrid Mount..."
    if ! grep -q "\[rules.zgameprops-framework\]" "$HM_CONFIG"; then
        echo -e "\n[rules.zgameprops-framework]\ndisabled = true" >> "$HM_CONFIG"
        log_msg "  Rule added to Hybrid Mount."
    else
        log_msg "  Rule already exists in Hybrid Mount."
    fi
fi

if [ ! -f "$MODPATH/patcher.jar" ]; then
    log_msg "! patcher.jar missing."
    abort "! patcher.jar missing."
fi

# Determine source jar (prefer backup if it exists and differs from current)
PATCH_SOURCE="$FRAMEWORK_JAR"
mkdir -p "$BACKUP_DIR"

if [ -f "$BACKUP_JAR" ]; then
    SRC_HASH=$(sha256sum "$FRAMEWORK_JAR" 2>/dev/null | cut -d' ' -f1)
    BAK_HASH=$(sha256sum "$BACKUP_JAR" 2>/dev/null | cut -d' ' -f1)
    log_msg "- Current: $SRC_HASH"
    log_msg "- Backup:  $BAK_HASH"
    
    if [ "$SRC_HASH" != "$BAK_HASH" ]; then
        log_msg "- Current framework differs from backup. Using BACKUP (stock)."
        PATCH_SOURCE="$BACKUP_JAR"
    else
        log_msg "- Backup matches current. Using current."
    fi
else
    log_msg "- Creating initial backup from current framework.jar."
    cp "$FRAMEWORK_JAR" "$BACKUP_JAR"
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
    rm -f "$PATCHER_JAR" "$WORK_JAR" "$WORK_JAR_OUT"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful!"
log_msg "- IMPORTANT: If you bootloop, clear Dalvik cache via recovery/ADB!"
mv "$WORK_JAR_OUT" "$OUT_DIR/framework.jar"
rm -f "$PATCHER_JAR" "$WORK_JAR"
