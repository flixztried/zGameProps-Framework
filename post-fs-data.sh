#!/system/bin/sh
MODDIR=${0%/*}

if [ -f "$MODDIR/boot_attempted" ]; then
    if [ -f "$MODDIR/boot_failed_once" ]; then
        touch "$MODDIR/disable"
        rm -f "$MODDIR/boot_failed_once"
        rm -f "$MODDIR/boot_attempted"
        exit 0
    else
        touch "$MODDIR/boot_failed_once"
    fi
fi

touch "$MODDIR/boot_attempted"

(
    sleep 120
    rm -f "$MODDIR/boot_attempted"
    rm -f "$MODDIR/boot_failed_once"
) &
