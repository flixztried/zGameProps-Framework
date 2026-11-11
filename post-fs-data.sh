#!/system/bin/sh
MODDIR=${0%/*}

(
    while true; do
        if [ "$(getprop sys.boot_completed)" = "1" ]; then
            rm -f "$MODDIR/boot_failed_once"
            exit 0
        fi
        sleep 1
    done
) &

if [ -f "$MODDIR/boot_failed_once" ]; then
    touch "$MODDIR/disable"
    rm -f "$MODDIR/boot_failed_once"
    exit 0
fi

touch "$MODDIR/boot_failed_once"
