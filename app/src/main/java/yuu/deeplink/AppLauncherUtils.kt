package yuu.deeplink

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

object AppLauncherUtils {
    const val jd = "jing_dong"
    const val tb = "tao_bao"
    const val tm = "tian_mao"
    const val amazon = "amazon"
    const val pdd = "pin_duo_duo"
    private val shopData = hashMapOf(
            jd to ShopInfo(packageName = "com.jingdong.app.mall", url = "https://mall.jd.com/index-1000000127.html", displayName = "京东"),
            tb to ShopInfo(packageName = "com.taobao.taobao", url = "https://apple.tmall.com/", displayName = "淘宝"),
            tm to ShopInfo(packageName = "com.tmall.wireless", url = "https://apple.tmall.com/", displayName = "天猫"),
            amazon to ShopInfo(packageName = "com.amazon.mShop.android.shopping", url = " https://www.amazon.com/stores/Apple/page/77D9E1F7-0337-4282-9DB6-B6B8FB2DC98D", displayName = "亚马逊"),
            pdd to ShopInfo(packageName = "com.xunmeng.pinduoduo", url = "https://mobile.yangkeduo.com/goods2.html?ps=z0yk81K35c", displayName = "亚马逊中国")
    )
    private val SHOP_PRIORITY_ORDER = listOf(jd, tb, tm, amazon, pdd)

    private fun checkApkExist(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    // 使用浏览器打开URL
    private fun openUrlByBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openShop(context: Context, shopKey: String) {
        shopData[shopKey]?.let { shopInfo ->
            openShop(context, shopInfo.url, shopInfo.packageName)
        } ?: run {
            Log.e("yuu", "openShop $shopKey 打开失败")
        }
    }

    fun openShop(context: Context) {
        for (shopKey in SHOP_PRIORITY_ORDER) {
            shopData[shopKey]?.let { shopInfo ->
                if (openShop(context, shopInfo.url, shopInfo.packageName, false)) {
                    Log.e("yuu", "openShop SHOP_PRIORITY_ORDER $shopKey 打开")
                    return
                }
            }
        }
        Log.e("yuu", "openShop 没有可用的购物应用")
        Toast.makeText(context, "没有可用的购物应用", Toast.LENGTH_SHORT).show()
    }

    private fun openShop(context: Context, url: String, packageName: String, isShowToast: Boolean? = true): Boolean {
        if (!checkApkExist(context, packageName)) {
            Log.e("yuu", "openShop $packageName 不存在, 可能没有安装或者没有在 AndroidManifest <queries> 中添加包名")
            if (isShowToast == true) Toast.makeText(context, "$packageName 未找到", Toast.LENGTH_SHORT).show()
            openUrlByBrowser(context, url)
            return false
        }

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = url.toUri()
                `package` = packageName
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                Log.e("yuu", "openShop intent为null,可能是 $packageName 没有实现 <data android:scheme=\"https\"> 属性")
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

data class ShopInfo(val packageName: String, val url: String, val displayName: String)