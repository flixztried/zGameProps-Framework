# zGameProps Framework Patcher

A systemless `framework.jar` patcher that injects `GamesPropsUtils` directly into the Android framework.

## ⚠️ Warning
This module modifies core Android framework files via a custom bind-mount overlay. **Flashing this is at your own risk.** 

**First boot will be slow.** ART discards the precompiled boot image when `framework.jar` changes. Do not pull the battery; wait for the system to rebuild the cache.

## How it works
1. Extracts the stock `framework.jar`.
2. Finds the specific `.dex` file containing `android.app.Instrumentation`.
3. Patches the bytecode to inject `GamesPropsUtils` when an app starts.
4. Sets SELinux context to `system_file`, then uses `mount --bind` during `post-fs-data` to overlay the patched file.

The app process itself has **no runtime hooks** injected into its memory.

## Recovery
If you bootloop, the module has an active watchdog that will auto-disable and reboot after 180 seconds. If you need to manually disable it:
- **APatch/Magisk:** Create a file named `disable` inside `/data/adb/modules/zgameprops-framework/` (note the lowercase module ID).

*Note: OTA updates will overwrite the stock framework. You must re-flash this module after any system update.*
