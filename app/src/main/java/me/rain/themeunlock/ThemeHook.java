package me.rain.themeunlock;

import android.util.Base64;
import android.util.Log;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.libxposed.api.XposedModule;

public final class ThemeHook extends XposedModule {
    private static final String TARGET = "com.android.thememanager";
    private static final String TAG = "ThemeUnlock";
    private static final int MAX_ORIGINAL_LOGS = 32;
    private final AtomicInteger logCount = new AtomicInteger();

    @Override
    public void onPackageReady(PackageReadyParam param) {
        if (!TARGET.equals(param.getPackageName())) {
            return;
        }
        ClassLoader loader = param.getClassLoader();
        try {
            Method ld6 = Class.forName(
                "com.android.thememanager.controller.online.PurchaseManager", false, loader
            ).getDeclaredMethod("ld6", String.class, String.class);
            hook(ld6).intercept(chain -> {
                Object original = null;
                Throwable error = null;
                try {
                    original = chain.proceed();
                } catch (Throwable caught) {
                    error = caught;
                }
                int index = logCount.getAndIncrement();
                if (index < MAX_ORIGINAL_LOGS) {
                    try {
                        String value = error == null
                            ? String.valueOf(original)
                            : "throw:" + error.getClass().getName();
                        if (value.length() > 4096) {
                            value = value.substring(0, 4096);
                        }
                        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                        StringBuilder hex = new StringBuilder(bytes.length * 2);
                        for (byte b : bytes) {
                            int item = b & 0xFF;
                            hex.append(Character.forDigit(item >>> 4, 16));
                            hex.append(Character.forDigit(item & 15, 16));
                        }
                        log(Log.INFO, TAG, "ld6 original=" + value);
                        log(Log.INFO, TAG, "ld6 base64=" + Base64.encodeToString(bytes, Base64.NO_WRAP));
                        log(Log.INFO, TAG, "ld6 hex=" + hex);
                        if (index == 0) {
                            log(Log.INFO, TAG, "ld6 caller stack", new Throwable());
                        }
                    } catch (Throwable loggingError) {
                        log(Log.WARN, TAG, "ld6 logging failed");
                    }
                }
                return 1;
            });
            log(Log.INFO, TAG, "PurchaseManager.ld6 hooked");
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "PurchaseManager.ld6 hook failed", e);
        }

        try {
            Method mcp = Class.forName(
                "com.android.thememanager.basemodule.account.LoginManager", false, loader
            ).getDeclaredMethod("mcp");
            hook(mcp).intercept(chain -> true);
            log(Log.INFO, TAG, "LoginManager.mcp hooked");
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "LoginManager.mcp hook failed", e);
        }

        try {
            Method zy = Class.forName(
                "com.android.thememanager.util.jk", false, loader
            ).getDeclaredMethod("zy");
            hook(zy).intercept(chain -> true);
            log(Log.INFO, TAG, "jk.zy hooked");
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "jk.zy hook failed", e);
        }
    }
}
