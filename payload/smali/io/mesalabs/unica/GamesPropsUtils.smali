.class public final Lio/mesalabs/unica/GamesPropsUtils;
.super Ljava/lang/Object;
.source "GamesPropsUtils.java"

# static fields
.field private static final TAG:Ljava/lang/String; = "zGameProps"

.field private static final packagesToChange:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = { "Ljava/util/Map<", "Ljava/lang/String;", "[", "Ljava/lang/String;", ">;" }
    .end annotation
.end field

.field private static final propsToChange:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = { "Ljava/util/Map<", "Ljava/lang/String;", "Ljava/util/Map<", "Ljava/lang/String;", "Ljava/lang/String;", ">;>;" }
    .end annotation
.end field

.method static constructor <clinit>()V
    .locals 8
    new-instance v0, Ljava/util/HashMap;
    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V
    sput-object v0, Lio/mesalabs/unica/GamesPropsUtils;->propsToChange:Ljava/util/Map;
    new-instance v1, Ljava/util/HashMap;
    invoke-direct {v1}, Ljava/util/HashMap;-><init>()V
    sput-object v1, Lio/mesalabs/unica/GamesPropsUtils;->packagesToChange:Ljava/util/Map;
    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "blackshark"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "penrose"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "blackshark"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "SHARK PRS-A0"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "BS4"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.proximabeta.mf.uamo"
    aput-object v4, v2, v3
    const-string v3, "BS4"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "POCO"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "marble"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "Xiaomi"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "23049PCD8G"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "F5"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x3
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.dts.freefiremax"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.dts.freefireth"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.mobile.legends"
    aput-object v4, v2, v3
    const-string v3, "F5"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "Xiaomi"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "vili"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "Xiaomi"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "2107113SI"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MI11TP"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x4
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.ea.gp.apexlegendsmobilefps"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.levelinfinite.hotta.gp"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.supercell.clashofclans"
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const-string v4, "com.vng.mlbbvn"
    aput-object v4, v2, v3
    const-string v3, "MI11TP"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "Xiaomi"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "nuwa"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "Xiaomi"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "2210132C"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MI13P"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x2
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.levelinfinite.sgameGlobal"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.tencent.tmgp.sgame"
    aput-object v4, v2, v3
    const-string v3, "MI13P"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "OnePlus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "OnePlus8Pro"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "OnePlus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "IN2020"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "OP8P"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x7
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.netease.lztgglobal"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.riotgames.league.wildrift"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.riotgames.league.wildrifttw"
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const-string v4, "com.riotgames.league.wildriftvn"
    aput-object v4, v2, v3
    const/4 v3, 0x4
    const-string v4, "com.riotgames.league.teamfighttactics"
    aput-object v4, v2, v3
    const/4 v3, 0x5
    const-string v4, "com.riotgames.league.teamfighttacticstw"
    aput-object v4, v2, v3
    const/4 v3, 0x6
    const-string v4, "com.riotgames.league.teamfighttacticsvn"
    aput-object v4, v2, v3
    const-string v3, "OP8P"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "OnePlus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "OnePlus9Pro"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "OnePlus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "LE2123"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "OP9P"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x3
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.epicgames.fortnite"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.epicgames.portal"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.tencent.lolm"
    aput-object v4, v2, v3
    const-string v3, "OP9P"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "asus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "AI2201"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "asus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "ASUS_AI2201"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "ROG6"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x5
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.ea.gp.fifamobile"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.gameloft.android.ANMP.GloftA9HM"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.madfingergames.legends"
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const-string v4, "com.pearlabyss.blackdesertm.gl"
    aput-object v4, v2, v3
    const/4 v3, 0x4
    const-string v4, "com.pearlabyss.blackdesertm"
    aput-object v4, v2, v3
    const-string v3, "ROG6"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "asus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "AI2203_C"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "asus"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "ASUS_AI2203_C"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "ROG6D"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.proxima.dfm"
    aput-object v4, v2, v3
    const-string v3, "ROG6D"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "samsung"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "e3q"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "samsung"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "SM-S928B"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "S24U"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x7
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.blizzard.diablo.immortal"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.pubg.imobile"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.pubg.krmobile"
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const-string v4, "com.rekoo.pubgm"
    aput-object v4, v2, v3
    const/4 v3, 0x4
    const-string v4, "com.tencent.ig"
    aput-object v4, v2, v3
    const/4 v3, 0x5
    const-string v4, "com.tencent.tmgp.pubgmhd"
    aput-object v4, v2, v3
    const/4 v3, 0x6
    const-string v4, "com.vng.pubgmobile"
    aput-object v4, v2, v3
    const-string v3, "S24U"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Ljava/util/HashMap;
    invoke-direct {v2}, Ljava/util/HashMap;-><init>()V

    const-string v3, "BRAND"
    const-string v4, "Lenovo"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "DEVICE"
    const-string v4, "TB-9707F"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MANUFACTURER"
    const-string v4, "Lenovo"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "MODEL"
    const-string v4, "Lenovo TB-9707F"
    invoke-interface {v2, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v3, "LenovoY700"
    invoke-interface {v0, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const/4 v2, 0x4
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    const-string v4, "com.activision.callofduty.shooter"
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.garena.game.codm"
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const-string v4, "com.tencent.tmgp.kr.codm"
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const-string v4, "com.vng.codmvn"
    aput-object v4, v2, v3
    const-string v3, "LenovoY700"
    invoke-interface {v1, v3, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method private constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static setProps(Landroid/content/Context;)V
    .locals 8

    :try_start_0
    if-nez p0, :cond_0
    return-void

    :cond_0
    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;
    move-result-object v0

    if-eqz v0, :cond_1
    invoke-virtual {v0}, Ljava/lang/String;->isEmpty()Z
    move-result v1
    if-eqz v1, :cond_2

    :cond_1
    return-void

    :cond_2
    sget-object v1, Lio/mesalabs/unica/GamesPropsUtils;->packagesToChange:Ljava/util/Map;
    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;
    move-result-object v1
    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;
    move-result-object v1

    :cond_3
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z
    move-result v2
    if-eqz v2, :cond_4

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;
    move-result-object v2
    check-cast v2, Ljava/util/Map$Entry;

    invoke-interface {v2}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;
    move-result-object v3
    check-cast v3, Ljava/lang/String;

    invoke-interface {v2}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;
    move-result-object v2
    check-cast v2, [Ljava/lang/String;

    array-length v4, v2
    const/4 v5, 0x0

    :goto_0
    if-ge v5, v4, :cond_3
    aget-object v6, v2, v5
    invoke-virtual {v6, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v7
    if-eqz v7, :cond_5

    sget-object v1, Lio/mesalabs/unica/GamesPropsUtils;->propsToChange:Ljava/util/Map;
    invoke-interface {v1, v3}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v1
    check-cast v1, Ljava/util/Map;

    if-eqz v1, :cond_4
    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;
    move-result-object v1
    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;
    move-result-object v1

    :goto_1
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z
    move-result v2
    if-eqz v2, :cond_4

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;
    move-result-object v2
    check-cast v2, Ljava/util/Map$Entry;

    invoke-interface {v2}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;
    move-result-object v3
    check-cast v3, Ljava/lang/String;

    invoke-interface {v2}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;
    move-result-object v2
    check-cast v2, Ljava/lang/String;

    invoke-static {v3, v2}, Lio/mesalabs/unica/GamesPropsUtils;->setPropValue(Ljava/lang/String;Ljava/lang/String;)V
    goto :goto_1

    :cond_4
    return-void

    :cond_5
    add-int/lit8 v5, v5, 0x1
    goto :goto_0

    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    return-void

    :catch_0
    move-exception p0
    return-void
.end method

.method private static setPropValue(Ljava/lang/String;Ljava/lang/String;)V
    .locals 2

    :try_start_0
    const-class v0, Landroid/os/Build;
    invoke-virtual {v0, p0}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0

    const/4 v1, 0x1
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    const/4 v1, 0x0
    invoke-virtual {v0, v1, p1}, Ljava/lang/reflect/Field;->set(Ljava/lang/Object;Ljava/lang/Object;)V

    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0
    :catch_0
    return-void
.end method