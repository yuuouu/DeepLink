package yuu.deeplink

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri

import yuu.deeplink.R

object AppLauncherUtils {
    const val jd = "jing_dong"
    const val tb = "tao_bao"
    const val tm = "tian_mao"
    const val amazon = "amazon"
    const val pdd = "pin_duo_duo"
    private val shopData = hashMapOf(
        jd to ShopInfo(
            packageName = "com.jingdong.app.mall",
            url = "https://mall.jd.com/index-1000000127.html",
            displayNameRes = R.string.shop_name_jd
        ),
        tb to ShopInfo(
            packageName = "com.taobao.taobao",
            url = "https://apple.tmall.com/",
            displayNameRes = R.string.shop_name_taobao
        ),
        tm to ShopInfo(
            packageName = "com.tmall.wireless",
            url = "https://apple.tmall.com/",
            displayNameRes = R.string.shop_name_tmall
        ),
        amazon to ShopInfo(
            packageName = "com.amazon.mShop.android.shopping",
            url = " https://www.amazon.com/stores/Apple/page/77D9E1F7-0337-4282-9DB6-B6B8FB2DC98D",
            displayNameRes = R.string.shop_name_amazon
        ),
        pdd to ShopInfo(
            packageName = "com.xunmeng.pinduoduo",
            url = "https://mobile.yangkeduo.com/goods2.html?ps=z0yk81K35c",
            displayNameRes = R.string.shop_name_amazon_cn
        )
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
            Log.e("yuu", context.getString(R.string.open_shop_failed, shopKey))
        }
    }

    fun openShop(context: Context) {
        for (shopKey in SHOP_PRIORITY_ORDER) {
            shopData[shopKey]?.let { shopInfo ->
                if (openShop(context, shopInfo.url, shopInfo.packageName, false)) {
                    Log.e("yuu", context.getString(R.string.open_shop_priority, shopKey))
                    return
                }
            }
        }
        Log.e("yuu", context.getString(R.string.open_shop_no_available))
        Toast.makeText(context, context.getString(R.string.toast_no_available_shops), Toast.LENGTH_SHORT).show()
    }

    private fun openShop(context: Context, url: String, packageName: String, isShowToast: Boolean? = true): Boolean {
        if (!checkApkExist(context, packageName)) {
            Log.e("yuu", context.getString(R.string.open_shop_package_missing, packageName))
            if (isShowToast == true) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_package_not_found, packageName),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                Log.e("yuu", context.getString(R.string.open_shop_intent_null, packageName))
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

data class ShopInfo(val packageName: String, val url: String, @StringRes val displayNameRes: Int)