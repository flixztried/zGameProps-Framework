# zGameProps Framework Patcher

A systemless `framework.jar` patcher that injects `GamesPropsUtils` directly into the Android framework.

## Status: STABLE
- **Games:** 120 FPS / High Graphics unlocked via Build field spoofing.
- **Google Apps:** Fully working (injection point moved to the end of `newApplication` to prevent GMS deadlocks).
- **Boot:** Fast (manual `dex2oat` handles cache rebuilding).

## How it works
1. Extracts the stock `framework.jar`.
2. Finds the specific `.dex` file containing `android.app.Instrumentation`.
3. Injects `GamesPropsUtils` right before the `return-object` instruction.
4. Bind-mounts the patched file over the original.
