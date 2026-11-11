#!/system/bin/sh
SKIPUNZIP=0

LOG_FILE="/sdcard/Documents/zGameProps_install_log.txt"
FALLBACK_LOG="/data/local/tmp/zGameProps_install_log.txt"

echo "--- zGameProps Installation Log ---" > "$LOG_FILE" 2>/dev/null || echo "" > "$FALLBACK_LOG"
echo "Date: $(date)" >> "$LOG_FILE" 2>/dev/null || echo "Date: $(date)" >> "$FALLBACK_LOG"
echo "Patcher Version: 1.0.0 (dexlib2 3.0.9)" >> "$LOG_FILE" 2>/dev/null || echo "Patcher Version: 1.0.0 (dexlib2 3.0.9)" >> "$FALLBACK_LOG"

log_msg() {
    ui_print "$1"
    echo "$1" >> "$LOG_FILE" 2>/dev/null || echo "$1" >> "$FALLBACK_LOG"
}

FRAMEWORK_JAR="/system/framework/framework.jar"
BACKUP_DIR="/data/adb/zgameprops_framework_backup"
WORK_JAR="/data/local/tmp/framework_work.jar"
WORK_JAR_OUT="/data/local/tmp/framework_patched.jar"
PATCHER_JAR="/data/local/tmp/patcher.jar"
OUT_DIR="$MODPATH/system/framework"

API_LEVEL=$(getprop ro.build.version.sdk)
JAR_SIZE=$(ls -l "$FRAMEWORK_JAR" | awk '{print $5}')

log_msg "- zGameProps Framework Patcher"
log_msg "- Device API Level: $API_LEVEL"
log_msg "- Stock framework.jar size: $JAR_SIZE bytes"

if [ ! -f "$MODPATH/patcher.jar" ]; then
    log_msg "! patcher.jar missing."
    abort "! patcher.jar missing."
fi

log_msg "- Backing up stock framework.jar..."
mkdir -p "$BACKUP_DIR"
if [ ! -f "$BACKUP_DIR/framework.jar" ]; then
    cp "$FRAMEWORK_JAR" "$BACKUP_DIR/framework.jar"
    log_msg "  Backup created."
else
    log_msg "  Backup already exists."
fi

log_msg "- Preparing environment..."
mkdir -p "$OUT_DIR"
cp "$MODPATH/patcher.jar" "$PATCHER_JAR"
cp "$FRAMEWORK_JAR" "$WORK_JAR"
chmod 777 "$PATCHER_JAR" "$WORK_JAR"

log_msg "- Starting app_process..."
export CLASSPATH="$PATCHER_JAR"

app_process -Xmx1024m /system/bin com.zgameprops.Patcher "$WORK_JAR" "$WORK_JAR_OUT" "$API_LEVEL" >> "$LOG_FILE" 2>&1
PATCH_RESULT=$?

if [ $PATCH_RESULT -ne 0 ]; then
    log_msg "! Patching failed with exit code $PATCH_RESULT."
    log_msg "- Please check $LOG_FILE for details."
    rm -f "$PATCHER_JAR" "$WORK_JAR" "$WORK_JAR_OUT"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful! Reboot to apply."
log_msg "- WARNING: First boot will be slow. ART is rebuilding the cache."
mv "$WORK_JAR_OUT" "$OUT_DIR/framework.jar"
rm -f "$PATCHER_JAR" "$WORK_JAR"
