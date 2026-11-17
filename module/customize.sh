#!/system/bin/sh
SKIPUNZIP=0
API_LEVEL=$(getprop ro.build.version.sdk)
LOG_FILE="/data/local/tmp/zGameProps_install.log"
PATCHER="/data/local/tmp/patcher.jar"
WORK_JAR="/data/local/tmp/framework_stock.jar"
OUT_JAR="/data/local/tmp/framework_patched.jar"

log() { ui_print "$1"; echo "$1" >> "$LOG_FILE"; }
echo "--- zGameProps Universal Install ---" > "$LOG_FILE"
log "- Device API Level: $API_LEVEL"

case "$API_LEVEL" in
    30|31|32|33|34|35|36|37) ;;
    *) abort "! Unsupported Android API $API_LEVEL. This module supports API 30–37." ;;
esac

cp /system/framework/framework.jar "$WORK_JAR" || abort "! Cannot read stock framework.jar"

export CLASSPATH="$MODPATH/patcher.jar"
app_process -Xmx1024m /system/bin dev.kaorios.engine.cli.MainKt "$WORK_JAR" "$OUT_JAR" "$API_LEVEL" "$MODPATH/profiles.json" >> "$LOG_FILE" 2>&1

if [ $? -ne 0 ]; then
    log "! Patching failed — aborting (stock JAR untouched)"
    rm -f "$WORK_JAR" "$OUT_JAR"
    abort "! Aborted to prevent bootloop."
fi

mkdir -p "$MODPATH/system/framework"
mv "$OUT_JAR" "$MODPATH/system/framework/framework.jar"
rm -f "$WORK_JAR"
log "- Patching successful. Reboot to apply."
