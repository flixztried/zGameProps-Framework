#!/system/bin/sh
SKIPUNZIP=0

LOG_FILE="/sdcard/Documents/zGameProps_install_log.txt"
FALLBACK_LOG="/data/local/tmp/zGameProps_install_log.txt"

echo "--- zGameProps Installation Log ---" > "$LOG_FILE" 2>/dev/null || echo "" > "$FALLBACK_LOG"

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

mkdir -p "$BACKUP_DIR"
if [ -f "$BACKUP_JAR" ]; then
    # Verify backup doesn't contain our spoof class
    if unzip -l "$BACKUP_JAR" | grep -q "GamesPropsUtils"; then
        log_msg "- WARNING: Backup is already patched! Deleting it."
        rm -f "$BACKUP_JAR"
    else
        log_msg "- Backup is valid (stock)."
    fi
fi

if [ ! -f "$BACKUP_JAR" ]; then
    log_msg "- Creating initial backup from current framework.jar."
    cp "$FRAMEWORK_JAR" "$BACKUP_JAR"
fi

PATCH_SOURCE="$FRAMEWORK_JAR"
SRC_HASH=$(sha256sum "$FRAMEWORK_JAR" 2>/dev/null | cut -d' ' -f1)
BAK_HASH=$(sha256sum "$BACKUP_JAR" 2>/dev/null | cut -d' ' -f1)

if [ "$SRC_HASH" != "$BAK_HASH" ]; then
    log_msg "- Current framework differs from backup. Using BACKUP (stock)."
    PATCH_SOURCE="$BACKUP_JAR"
else
    log_msg "- Backup matches current. Using current."
fi

mkdir -p "$OUT_DIR"
cp "$MODPATH/patcher.jar" "$PATCHER_JAR"
cp "$PATCH_SOURCE" "$WORK_JAR"
chmod 777 "$PATCHER_JAR" "$WORK_JAR"

export CLASSPATH="$PATCHER_JAR"
app_process -Xmx1024m /system/bin com.zgameprops.Patcher "$WORK_JAR" "$WORK_JAR_OUT" "$API_LEVEL" >> "$LOG_FILE" 2>&1
PATCH_RESULT=$?

if [ $PATCH_RESULT -ne 0 ]; then
    log_msg "! Patching failed with exit code $PATCH_RESULT."
    rm -f "$PATCHER_JAR" "$WORK_JAR" "$WORK_JAR_OUT"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful!"
mv "$WORK_JAR_OUT" "$OUT_DIR/framework.jar"
rm -f "$PATCHER_JAR" "$WORK_JAR"
