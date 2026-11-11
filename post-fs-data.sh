#!/system/bin/sh
MODDIR=${0%/*}

# Background watchdog: clears failure marker only after real boot success
(
    while true; do
        if [ "$(getprop sys.boot_completed)" = "1" ]; then
            rm -f "$MODDIR/boot_failed_once"
            exit 0
        fi
        sleep 2
    done
) &

# If previous boot failed, disable now
if [ -f "$MODDIR/boot_failed_once" ]; then
    touch "$MODDIR/disable"
    rm -f "$MODDIR/boot_failed_once"
    exit 0
fi

touch "$MODDIR/boot_failed_once"
