package me.rain.themeunlock

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ThemeUnlockScreen() }
    }
}

private val lightPalette = lightColorScheme(
    primary = Color(0xFF346756),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4ECDD),
    onPrimaryContainer = Color(0xFF173B2D),
    background = Color(0xFFF7F8F5),
    surface = Color(0xFFF7F8F5),
    surfaceVariant = Color(0xFFE8ECE6),
    onSurface = Color(0xFF202924),
    onSurfaceVariant = Color(0xFF526058),
    outlineVariant = Color(0xFFD6DDD6)
)

private val darkPalette = darkColorScheme(
    primary = Color(0xFFA9D6BD),
    onPrimary = Color(0xFF133D2B),
    primaryContainer = Color(0xFF285743),
    onPrimaryContainer = Color(0xFFD0EFDD),
    background = Color(0xFF121713),
    surface = Color(0xFF121713),
    surfaceVariant = Color(0xFF26312A),
    onSurface = Color(0xFFE0EAE2),
    onSurfaceVariant = Color(0xFFBAC9BD),
    outlineVariant = Color(0xFF3F5145)
)

@Composable
private fun ThemeUnlockScreen() {
    val context = LocalContext.current
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkPalette else lightPalette) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { insets ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("THEME / MODULE", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("主题解锁", style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold)
                Text("Xiaomi Theme · 10.8.0.0", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("LSPosed 模块", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text(
                            "需要在 LSPosed 中启用，并将作用域设为「主题壁纸」。此页面不检测实时 Hook 状态。",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text("Hook 规则", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(horizontal = 20.dp, vertical = 7.dp)) {
                        RuleRow("购买状态", "ld6(String, String)", "记录原始结果 → 返回 1")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        RuleRow("登录状态", "mcp()", "返回 true")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        RuleRow("VIP 标记", "zy()", "返回 true")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    onClick = {
                        val launch = context.packageManager
                            .getLaunchIntentForPackage("com.android.thememanager")
                        if (launch != null) {
                            try {
                                context.startActivity(launch)
                            } catch (_: ActivityNotFoundException) {
                                // The ROM may provide no launchable theme activity.
                            } catch (_: SecurityException) {
                                // Never crash the module's own UI.
                            }
                        }
                    }
                ) {
                    Text("打开主题壁纸")
                }

                Text(
                    "安装后在 LSPosed 勾选模块及作用域，再强行停止并重新打开主题壁纸。原始值、Base64 / Hex、调用堆栈和 Hook 错误可在 LSPosed 日志查看（最多记录前 32 次）。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text("v1.0.0 · 仅适配给定方法签名",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun RuleRow(name: String, method: String, result: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
            Text(method, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(result, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary)
    }
}
