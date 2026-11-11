# zGameProps Framework Patcher

A systemless `framework.jar` patcher that injects `GamesPropsUtils` directly into the Android framework.

## ⚠️ Warning
This module modifies core Android framework files via systemless overlay. While the bootloop guard is designed to protect you, **flashing this is at your own risk**. 

**First boot will be slow.** ART discards the precompiled boot image when `framework.jar` changes. Do not pull the battery; wait for the system to rebuild the cache.

## How it works
1. Extracts the stock `framework.jar`.
2. Finds the specific `.dex` file containing `android.app.Instrumentation`.
3. Patches the bytecode to inject `GamesPropsUtils` when an app starts.
4. Rebuilds the JAR and overlays it via Magisk/KernelSU/APatch.

The app process itself has **no runtime hooks** injected into its memory.

## Recovery
If you bootloop, the module will auto-disable on the second failed boot. If you need to manually disable it:
- **Magisk:** Create a file named `disable` inside `/data/adb/modules/zGameProps-Framework/`.
- **APatch:** Use APatch's Safe Mode.

## Tested On
| Device | ROM | Android Version | Result |
| :--- | :--- | :--- | :--- |
| (Your Device) | (Your ROM) | Android 14 | ✅ Works |

*Note: OTA updates will overwrite the stock framework. You must re-flash this module after any system update.*
