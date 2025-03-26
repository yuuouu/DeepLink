package yuu.deeplink


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast

object AppLauncherUtils {
    private val SHOP_PRIORITY_ORDER = listOf(
        "jd",       // 京东
        "taobao",   // 淘宝
        "tmall",    // 天猫
        "amazon",   // 亚马逊国际
        "amazon_cn" // 亚马逊中国
    )
    private fun checkApkExist(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // 使用浏览器打开URL
    private fun openUrlByBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openShop(context: Context, shopData: HashMap<String, ShopInfo>) {
        for (shopKey in SHOP_PRIORITY_ORDER) {
            shopData[shopKey]?.let { shopInfo ->
                if (openShop(context, shopInfo)) {
                    Log.e("yuu", "openShop SHOP_PRIORITY_ORDER $shopKey 打开")
                    return
                }
            }
        }
        Log.e("yuu", "openShop 没有可用的购物应用")
        Toast.makeText(context, "没有可用的购物应用", Toast.LENGTH_SHORT).show()
    }

    private fun openShop(context: Context, shopInfo: ShopInfo): Boolean {
        return openShop(context, shopInfo.url, shopInfo.packageName)
    }

    fun openShop(context: Context, url: String, packageName: String): Boolean {
        if (!checkApkExist(context, packageName)) {
            Log.e("yuu", "openShop $packageName 不存在, 可能没有安装或者没有在 AndroidManifest 的<queries>中添加包名")
            Toast.makeText(context, "$packageName 未找到", Toast.LENGTH_SHORT).show()
            openUrlByBrowser(context, url)
            return false
        }

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(url)
                `package` = packageName
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                Log.e("yuu", "openShop intent为null,可能是目标包没有实现 data android:scheme=\"https\" 属性")
                openUrlByBrowser(context, url)
                false
            }
        } catch (e: Exception) {
            openUrlByBrowser(context, url)
            e.printStackTrace()
            false
        }
    }
}