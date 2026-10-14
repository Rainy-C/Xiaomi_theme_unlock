# Xiaomi Theme Unlock

针对 **主题壁纸 10.8.0.0** 的 LSPosed 模块，使用 Kotlin + Jetpack Compose Material Design 3 界面。

## Hook 规则

| 类 / 方法 | 参数 | 行为 |
|---|---|---|
| PurchaseManager.ld6 | (String, String) | 在 afterHookedMethod 记录原始返回值，再返回 int 1 |
| LoginManager.mcp | () | 在 beforeHookedMethod 返回 boolean true |
| com.android.thememanager.util.jk.zy | () | 在 beforeHookedMethod 返回 boolean true |

作用域是 `com.android.thememanager`。每项 Hook 独立捕获异常，LSPosed 日志使用 `[ThemeUnlock]` 前缀。`ld6` 最多记录前 32 次原始结果，同时输出 Base64、Hex 和调用堆栈，单次原始字符串截断到 4096 字符。界面不声称能够读取实际 Hook 启用状态。日志不记录方法参数，避免不必要的信息收集。

## 安装

上传仓库文件后进入 GitHub Actions → Build Release APK → 选择最新成功运行 → 下载 `XiaomiThemeUnlock-Release`。解压并安装 APK，在 LSPosed 中启用模块，将作用域勾选为「主题壁纸」，强行停止并重新打开目标应用。

适配依据是提供的 10.8.0.0 方法签名，其他 ROM / 版本未验证。如签名变化，请查看 LSPosed 日志。

## Release 工作流

推送至 `main` 或手动 `workflow_dispatch`。工作流**仅执行 `:app:assembleRelease`**、验证签名、上传 APK 和 SHA256 文件，不运行 Debug 构建。

如需稳定签名用于覆盖安装，请在 GitHub 仓库 Actions Secrets 中设置：

- `ANDROID_KEYSTORE_BASE64`：base64 编码的 keystore
- `ANDROID_KEYSTORE_PASSWORD`：keystore 密码
- `ANDROID_KEY_ALIAS`：密钥别名
- `ANDROID_KEY_PASSWORD`：密钥密码

未配置时使用临时 Release 签名，APK 可安装，但不同运行的签名不同，更新需要卸载旧 APK。不要把真实私钥提交到公开仓库。

配置：Android Gradle Plugin 8.13.2 / Gradle 8.13 / Kotlin 2.2.20 / JDK17 / compileSdk36。Xposed API 82 使用 `compileOnly`，不会捆绑到 APK。`./gradlew` wrapper 未包含在 ZIP 中，CI 使用 Gradle Actions 安装版本固定的 Gradle。
