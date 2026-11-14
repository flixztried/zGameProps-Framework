#!/system/bin/sh

MODDIR=${0%/*}
PATCHED_JAR="$MODDIR/system/framework/framework.jar"
STOCK_JAR="/system/framework/framework.jar"
LOG_FILE="/data/local/tmp/zGameProps_boot.log"

# --- Logger Function ---
log_msg() {
    echo "$(date): $1" >> "$LOG_FILE"
}

echo "--- zGameProps Boot Log ---" > "$LOG_FILE"
log_msg "post-fs-data.sh started."

# --- 1. Custom Bind Mount ---
if [ -f "$PATCHED_JAR" ]; then
    log_msg "Patched framework.jar found. Attempting bind mount..."
    
    # Wait briefly for /system to be fully mounted
    sleep 1
    
    if mount --bind "$PATCHED_JAR" "$STOCK_JAR"; then
        log_msg "SUCCESS: Bind mount for framework.jar is active."
        mount | grep framework.jar >> "$LOG_FILE"
    else
        log_msg "FAILURE: Bind mount for framework.jar failed."
    fi
else
    log_msg "Patched framework.jar not found in module directory."
fi

# --- 2. Active Bootloop Guard ---
# We use a background process to monitor boot status.
(
    # Wait up to 180 seconds (3 mins) for boot to complete.
    # ART cache rebuilding can take a long time on the first boot.
    for i in $(seq 1 180); do
        if [ "$(getprop sys.boot_completed)" = "1" ]; then
            # Boot completed successfully. Remove the failure marker.
            rm -f "$MODDIR/boot_failed_once"
            exit 0
        fi
        sleep 1
    done

    # If we reach here, boot did not complete in 180 seconds.
    # Create the disable file so the module is skipped next boot.
    touch "$MODDIR/disable"
    log_msg "WATCHDOG: Boot timeout reached. Disabled module and rebooting."
    
    # Sync filesystem and force reboot
    sync
    reboot
) &
