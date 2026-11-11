package io.mesalabs.unica;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class GamesPropsUtils {
    private static final String TAG = "zGameProps";
    private static final Map<String, String[]> packagesToChange = new HashMap<>();
    private static final Map<String, Map<String, String>> propsToChange = new HashMap<>();

    static {
        // Sample profile (Lenovo Y700) - Keeping strictly to the 4 UN1CA fields
        Map<String, String> y700 = new HashMap<>();
        y700.put("BRAND", "Lenovo");
        y700.put("DEVICE", "TB-9707F");
        y700.put("MANUFACTURER", "Lenovo");
        y700.put("MODEL", "Lenovo TB-9707F");
        
        propsToChange.put("LenovoY700", y700);
        packagesToChange.put("LenovoY700", new String[]{
            "com.activision.callofduty.shooter",
            "com.garena.game.codm",
            "com.tencent.tmgp.kr.codm",
            "com.vng.codmvn"
        });
    }

    private GamesPropsUtils() {}

    public static void setProps(Context context) {
        if (context == null) return;
        String packageName = context.getPackageName();
        if (packageName == null || packageName.isEmpty()) return;

        for (Map.Entry<String, String[]> entry : packagesToChange.entrySet()) {
            String profile = entry.getKey();
            for (String pkg : entry.getValue()) {
                if (pkg.equals(packageName)) {
                    Map<String, String> props = propsToChange.get(profile);
                    if (props != null) {
                        for (Map.Entry<String, String> prop : props.entrySet()) {
                            setPropValue(prop.getKey(), prop.getValue());
                        }
                    }
                    return;
                }
            }
        }
    }

    private static void setPropValue(String key, String value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
            // Log to confirm reflection works
            Log.d(TAG, "Spoofed Build." + key + " = " + value);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set " + key);
        }
    }
}
