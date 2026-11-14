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
        // 1. Black Shark 4
        Map<String, String> bs4 = new HashMap<>();
        bs4.put("BRAND", "blackshark");
        bs4.put("DEVICE", "penrose");
        bs4.put("MANUFACTURER", "blackshark");
        bs4.put("MODEL", "SHARK PRS-A0");
        propsToChange.put("BS4", bs4);
        packagesToChange.put("BS4", new String[]{
            "com.proximabeta.mf.uamo"
        });

        // 2. Poco F5
        Map<String, String> f5 = new HashMap<>();
        f5.put("BRAND", "POCO");
        f5.put("DEVICE", "marble");
        f5.put("MANUFACTURER", "Xiaomi");
        f5.put("MODEL", "23049PCD8G");
        propsToChange.put("F5", f5);
        packagesToChange.put("F5", new String[]{
            "com.dts.freefiremax",
            "com.dts.freefireth",
            "com.mobile.legends"
        });

        // 3. Xiaomi 11T Pro
        Map<String, String> mi11tp = new HashMap<>();
        mi11tp.put("BRAND", "Xiaomi");
        mi11tp.put("DEVICE", "vili");
        mi11tp.put("MANUFACTURER", "Xiaomi");
        mi11tp.put("MODEL", "2107113SI");
        propsToChange.put("MI11TP", mi11tp);
        packagesToChange.put("MI11TP", new String[]{
            "com.ea.gp.apexlegendsmobilefps",
            "com.levelinfinite.hotta.gp",
            "com.supercell.clashofclans",
            "com.vng.mlbbvn"
        });

        // 4. Xiaomi 13 Pro
        Map<String, String> mi13p = new HashMap<>();
        mi13p.put("BRAND", "Xiaomi");
        mi13p.put("DEVICE", "nuwa");
        mi13p.put("MANUFACTURER", "Xiaomi");
        mi13p.put("MODEL", "2210132C");
        propsToChange.put("MI13P", mi13p);
        packagesToChange.put("MI13P", new String[]{
            "com.levelinfinite.sgameGlobal",
            "com.tencent.tmgp.sgame"
        });

        // 5. OnePlus 8 Pro
        Map<String, String> op8p = new HashMap<>();
        op8p.put("BRAND", "OnePlus");
        op8p.put("DEVICE", "OnePlus8Pro");
        op8p.put("MANUFACTURER", "OnePlus");
        op8p.put("MODEL", "IN2020");
        propsToChange.put("OP8P", op8p);
        packagesToChange.put("OP8P", new String[]{
            "com.netease.lztgglobal",
            "com.riotgames.league.wildrift",
            "com.riotgames.league.wildrifttw",
            "com.riotgames.league.wildriftvn",
            "com.riotgames.league.teamfighttactics",
            "com.riotgames.league.teamfighttacticstw",
            "com.riotgames.league.teamfighttacticsvn"
        });

        // 6. OnePlus 9 Pro
        Map<String, String> op9p = new HashMap<>();
        op9p.put("BRAND", "OnePlus");
        op9p.put("DEVICE", "OnePlus9Pro");
        op9p.put("MANUFACTURER", "OnePlus");
        op9p.put("MODEL", "LE2123");
        propsToChange.put("OP9P", op9p);
        packagesToChange.put("OP9P", new String[]{
            "com.epicgames.fortnite",
            "com.epicgames.portal",
            "com.tencent.lolm"
        });

        // 7. ROG Phone 6
        Map<String, String> rog6 = new HashMap<>();
        rog6.put("BRAND", "asus");
        rog6.put("DEVICE", "AI2201");
        rog6.put("MANUFACTURER", "asus");
        rog6.put("MODEL", "ASUS_AI2201");
        propsToChange.put("ROG6", rog6);
        packagesToChange.put("ROG6", new String[]{
            "com.ea.gp.fifamobile",
            "com.gameloft.android.ANMP.GloftA9HM",
            "com.madfingergames.legends",
            "com.pearlabyss.blackdesertm.gl",
            "com.pearlabyss.blackdesertm"
        });

        // 8. ROG Phone 6D
        Map<String, String> rog6d = new HashMap<>();
        rog6d.put("BRAND", "asus");
        rog6d.put("DEVICE", "AI2203_C");
        rog6d.put("MANUFACTURER", "asus");
        rog6d.put("MODEL", "ASUS_AI2203_C");
        propsToChange.put("ROG6D", rog6d);
        packagesToChange.put("ROG6D", new String[]{
            "com.proxima.dfm"
        });

        // 9. Samsung S24 Ultra
        Map<String, String> s24u = new HashMap<>();
        s24u.put("BRAND", "samsung");
        s24u.put("DEVICE", "e3q");
        s24u.put("MANUFACTURER", "samsung");
        s24u.put("MODEL", "SM-S928B");
        propsToChange.put("S24U", s24u);
        packagesToChange.put("S24U", new String[]{
            "com.blizzard.diablo.immortal",
            "com.pubg.imobile",
            "com.pubg.krmobile",
            "com.rekoo.pubgm",
            "com.tencent.ig",
            "com.tencent.tmgp.pubgmhd",
            "com.vng.pubgmobile"
        });

        // 10. Lenovo Y700
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
        try {
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
        } catch (Throwable t) {
            Log.e(TAG, "Fatal error in setProps: " + t.getMessage());
        }
    }

    private static void setPropValue(String key, String value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
            Log.i(TAG, "Spoofed Build." + key + " = " + value);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to set " + key);
        }
    }
}
