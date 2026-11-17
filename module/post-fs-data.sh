#!/system/bin/sh
MODDIR=${0%/*}
PATCHED="$MODDIR/system/framework/framework.jar"
STOCK="/system/framework/framework.jar"
LOG="/data/local/tmp/zGameProps_boot.log"
log() { echo "$(date): $1" >> "$LOG"; }
echo "--- zGameProps Boot ---" > "$LOG"
if [ -f "$MODDIR/boot_attempted" ]; then
    log "Previous boot failed — disabling."
    touch "$MODDIR/disable"
    rm -f "$MODDIR/boot_attempted"
    exit 0
fi
touch "$MODDIR/boot_attempted"
if [ -f "$PATCHED" ]; then
    chcon u:object_r:system_file:s0 "$PATCHED"
    chmod 644 "$PATCHED"
    chown root:root "$PATCHED"
    sleep 1
    if mount --bind "$PATCHED" "$STOCK"; then
        log "SUCCESS: Bind mount active."
    else
        log "FAILURE: Bind mount failed."
    fi
fi
