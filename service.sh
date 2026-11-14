#!/system/bin/sh
MODDIR=${0%/*}
LOG_FILE="/data/local/tmp/zGameProps_boot.log"

(
    while true; do
        if [ "$(getprop sys.boot_completed)" = "1" ]; then
            rm -f "$MODDIR/boot_attempted"
            echo "$(date): Boot completed. Marker cleared." >> "$LOG_FILE"
            exit 0
        fi
        sleep 2
    done
) &
