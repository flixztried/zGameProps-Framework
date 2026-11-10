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
WORK_JAR="/data/local/tmp/framework_work.jar"

log_msg "- zGameProps Framework Patcher"
log_msg "- Checking prerequisites..."

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

log_msg "- Patching framework.jar on the fly..."
cp "$FRAMEWORK_JAR" "$WORK_JAR"

log_msg "- Starting app_process..."
export CLASSPATH="$MODPATH/patcher.jar"

# Capture all output from the patcher
app_process -J-Xmx1024m /system/bin com.zgameprops.Patcher "$WORK_JAR" "$MODPATH/system/framework/framework.jar" >> "$LOG_FILE" 2>&1
PATCH_RESULT=$?

if [ $PATCH_RESULT -ne 0 ]; then
    log_msg "! Patching failed with exit code $PATCH_RESULT."
    rm -f "$MODPATH/system/framework/framework.jar"
    rm -f "$WORK_JAR"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful! Reboot to apply."
rm -f "$WORK_JAR"
