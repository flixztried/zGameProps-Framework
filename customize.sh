#!/system/bin/sh
SKIPUNZIP=0

FRAMEWORK_JAR="/system/framework/framework.jar"
BACKUP_DIR="/data/adb/zgameprops_framework_backup"
WORK_JAR="/data/local/tmp/framework_work.jar"

ui_print "- zGameProps Framework Patcher"
ui_print "- Checking prerequisites..."

if [ ! -f "$MODPATH/patcher.dex" ]; then
    abort "! patcher.dex missing. Please build via GitHub Actions."
fi

ui_print "- Backing up stock framework.jar..."
mkdir -p "$BACKUP_DIR"
if [ ! -f "$BACKUP_DIR/framework.jar" ]; then
    cp "$FRAMEWORK_JAR" "$BACKUP_DIR/framework.jar"
fi

ui_print "- Patching framework.jar on the fly..."
cp "$FRAMEWORK_JAR" "$WORK_JAR"

# Execute Patcher.dex natively via app_process (no Java required)
CLASSPATH="$MODPATH/patcher.dex" app_process /system/bin com.zgameprops.Patcher "$WORK_JAR" "$MODPATH/system/framework/framework.jar"

if [ $? -ne 0 ]; then
    ui_print "! Patching failed. Reverting."
    rm -f "$MODPATH/system/framework/framework.jar"
    abort "! Aborted to prevent bootloop."
fi

rm -f "$WORK_JAR"
ui_print "- Patching successful! Reboot to apply."
