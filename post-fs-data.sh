#!/system/bin/sh

MODDIR=${0%/*}
PATCHED_JAR="$MODDIR/system/framework/framework.jar"
STOCK_JAR="/system/framework/framework.jar"
LOG_FILE="/data/local/tmp/zGameProps_boot.log"

log_msg() { echo "$(date): $1" >> "$LOG_FILE"; }
echo "--- zGameProps Boot Log ---" > "$LOG_FILE"
log_msg "post-fs-data.sh started."

# --- 1. Fix SELinux Labels & Mount ---
if [ -f "$PATCHED_JAR" ]; then
    log_msg "Setting SELinux context to system_file..."
    chcon u:object_r:system_file:s0 "$PATCHED_JAR" 2>> "$LOG_FILE"
    chmod 644 "$PATCHED_JAR" 2>> "$LOG_FILE"
    chown root:root "$PATCHED_JAR" 2>> "$LOG_FILE"

    log_msg "Attempting bind mount..."
    sleep 1
    
    if mount --bind "$PATCHED_JAR" "$STOCK_JAR"; then
        log_msg "SUCCESS: Bind mount active."
    else
        log_msg "FAILURE: Bind mount failed."
    fi
else
    log_msg "Patched framework.jar not found."
fi

# --- 2. Watchdog ---
(
    for i in $(seq 1 180); do
        if [ "$(getprop sys.boot_completed)" = "1" ]; then
            rm -f "$MODDIR/boot_failed_once"
            exit 0
        fi
        sleep 1
    done
    touch "$MODDIR/disable"
    log_msg "WATCHDOG: Boot timeout reached. Rebooting via init."
    # Use init to reboot safely
    setprop sys.powerctl reboot
) &
