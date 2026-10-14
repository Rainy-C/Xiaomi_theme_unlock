package me.rain.themeunlock;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class ThemeHook implements IXposedHookLoadPackage {
    private static final String TARGET = "com.android.thememanager";
    private static final String TAG = "[ThemeUnlock]";
    private static final int MAX_ORIGINAL_LOGS = 32;
    private static final AtomicInteger logged = new AtomicInteger();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!TARGET.equals(loadPackageParam.packageName)) {
            return;
        }

        hookPurchase(loadPackageParam.classLoader);
        hookLogin(loadPackageParam.classLoader);
        hookVip(loadPackageParam.classLoader);
    }

    private static void hookPurchase(ClassLoader loader) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.thememanager.controller.online.PurchaseManager",
                loader,
                "ld6",
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        // Observe the actual result before changing the caller-visible result.
                        int index = logged.getAndIncrement();
                        if (index < MAX_ORIGINAL_LOGS) {
                            String original = param.hasThrowable()
                                ? "throw:" + param.getThrowable().getClass().getName()
                                : String.valueOf(param.getResult());
                            if (original.length() > 4096) {
                                original = original.substring(0, 4096);
                            }
                            byte[] bytes = original.getBytes(StandardCharsets.UTF_8);
                            StringBuilder hex = new StringBuilder(bytes.length * 2);
                            for (byte b : bytes) {
                                int v = b & 0xFF;
                                hex.append(Character.forDigit(v >>> 4, 16));
                                hex.append(Character.forDigit(v & 15, 16));
                            }
                            XposedBridge.log(TAG + " ld6 original=" + original);
                            XposedBridge.log(TAG + " ld6 base64="
                                + Base64.encodeToString(bytes, Base64.NO_WRAP));
                            XposedBridge.log(TAG + " ld6 hex=" + hex);
                            XposedBridge.log(new Throwable(TAG + " ld6 caller stack"));
                        }
                        param.setResult(1);
                    }
                }
            );
            XposedBridge.log(TAG + " PurchaseManager.ld6(String,String) hooked");
        } catch (Throwable error) {
            XposedBridge.log(TAG + " ld6 hook failed");
            XposedBridge.log(error);
        }
    }

    private static void hookLogin(ClassLoader loader) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.thememanager.basemodule.account.LoginManager",
                loader,
                "mcp",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.setResult(true);
                    }
                }
            );
            XposedBridge.log(TAG + " LoginManager.mcp() hooked");
        } catch (Throwable error) {
            XposedBridge.log(TAG + " mcp hook failed");
            XposedBridge.log(error);
        }
    }

    private static void hookVip(ClassLoader loader) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.thememanager.util.jk",
                loader,
                "zy",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.setResult(true);
                    }
                }
            );
            XposedBridge.log(TAG + " jk.zy() hooked");
        } catch (Throwable error) {
            XposedBridge.log(TAG + " zy hook failed");
            XposedBridge.log(error);
        }
    }
}
