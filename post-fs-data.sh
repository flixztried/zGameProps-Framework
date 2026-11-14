#!/system/bin/sh

MODDIR=${0%/*}
PATCHED_JAR="$MODDIR/system/framework/framework.jar"
STOCK_JAR="/system/framework/framework.jar"
LOG_FILE="/data/local/tmp/zGameProps_boot.log"

log_msg() { echo "$(date): $1" >> "$LOG_FILE"; }
echo "--- zGameProps Boot Log ---" > "$LOG_FILE"

# 1. Marker watchdog
if [ -f "$MODDIR/boot_attempted" ]; then
    log_msg "ERROR: Previous boot did not complete. Disabling module."
    touch "$MODDIR/disable"
    rm -f "$MODDIR/boot_attempted"
    exit 0
fi
touch "$MODDIR/boot_attempted"

# 2. Fix SELinux Labels & Mount
if [ -f "$PATCHED_JAR" ]; then
    chcon u:object_r:system_file:s0 "$PATCHED_JAR" 2>> "$LOG_FILE"
    chmod 644 "$PATCHED_JAR" 2>> "$LOG_FILE"
    chown root:root "$PATCHED_JAR" 2>> "$LOG_FILE"

    sleep 1
    if mount --bind "$PATCHED_JAR" "$STOCK_JAR"; then
        log_msg "SUCCESS: Bind mount active."
    else
        log_msg "FAILURE: Bind mount failed."
    fi
fi

# 3. Manually regenerate the boot image in the background (Fixes 15-minute boot)
(
    sleep 5
    log_msg "Attempting manual dex2oat boot image generation..."
    
    rm -f /system/framework/arm64/boot-framework.art
    rm -f /system/framework/arm64/boot-framework.oat
    rm -f /system/framework/arm64/boot-framework.vdex
    
    dex2oat64 --image=/system/framework/arm64/boot-framework.art \
              --oat-file=/system/framework/arm64/boot-framework.oat \
              --dex-file=/system/framework/framework.jar \
              --instruction-set=arm64 \
              --instruction-set-features=default \
              --runtime-arg -Xms64m --runtime-arg -Xmx512m >> "$LOG_FILE" 2>&1

    if [ $? -eq 0 ]; then
        log_msg "SUCCESS: Boot image regenerated."
    else
        log_msg "FAILURE: dex2oat failed."
    fi
) &
