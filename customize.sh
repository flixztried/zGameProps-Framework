#!/system/bin/sh
SKIPUNZIP=0

# Define log paths (try standard Documents, fallback to local tmp)
LOG_FILE="/sdcard/Documents/zGameProps_install_log.txt"
FALLBACK_LOG="/data/local/tmp/zGameProps_install_log.txt"

# Initialize log
echo "--- zGameProps Installation Log ---" > "$LOG_FILE" 2>/dev/null || echo "" > "$FALLBACK_LOG"
echo "Date: $(date)" >> "$LOG_FILE" 2>/dev/null || echo "Date: $(date)" >> "$FALLBACK_LOG"

# Logging function
log_msg() {
    ui_print "$1"
    echo "$1" >> "$LOG_FILE" 2>/dev/null || echo "$1" >> "$FALLBACK_LOG"
}

log_cmd() {
    echo ">>> Running: $1" >> "$LOG_FILE" 2>/dev/null || echo ">>> Running: $1" >> "$FALLBACK_LOG"
    eval "$1" >> "$LOG_FILE" 2>&1 || eval "$1" >> "$FALLBACK_LOG" 2>&1
    local res=$?
    if [ $res -ne 0 ]; then
        echo ">>> Command failed with code $res" >> "$LOG_FILE" 2>/dev/null || echo ">>> Command failed with code $res" >> "$FALLBACK_LOG"
    fi
    return $res
}

FRAMEWORK_JAR="/system/framework/framework.jar"
BACKUP_DIR="/data/adb/zgameprops_framework_backup"
WORK_JAR="/data/local/tmp/framework_work.jar"

log_msg "- zGameProps Framework Patcher"
log_msg "- Checking prerequisites..."

if [ ! -f "$MODPATH/patcher.dex" ]; then
    log_msg "! patcher.dex missing. Please build via GitHub Actions."
    abort "! patcher.dex missing."
fi

log_msg "- Backing up stock framework.jar..."
mkdir -p "$BACKUP_DIR"
if [ ! -f "$BACKUP_DIR/framework.jar" ]; then
    cp "$FRAMEWORK_JAR" "$BACKUP_DIR/framework.jar"
    log_msg "  Backup created at $BACKUP_DIR/framework.jar"
else
    log_msg "  Backup already exists."
fi

log_msg "- Patching framework.jar on the fly..."
cp "$FRAMEWORK_JAR" "$WORK_JAR"

# Run the patcher with a 1GB heap to prevent OOM killer
log_msg "- Starting app_process (Heap limit: 1024m)..."
export CLASSPATH="$MODPATH/patcher.dex"

# We capture the specific output of app_process
app_process -J-Xmx1024m /system/bin com.zgameprops.Patcher "$WORK_JAR" "$MODPATH/system/framework/framework.jar" >> "$LOG_FILE" 2>&1
PATCH_RESULT=$?

if [ $PATCH_RESULT -ne 0 ]; then
    log_msg "! Patching failed with exit code $PATCH_RESULT."
    log_msg "! Check the log file for details."
    rm -f "$MODPATH/system/framework/framework.jar"
    rm -f "$WORK_JAR"
    abort "! Aborted to prevent bootloop."
fi

log_msg "- Patching successful! Reboot to apply."
rm -f "$WORK_JAR"
log_msg "--- End of Log ---"
