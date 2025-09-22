package io.github.yuuouu.deeolink

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri

/**
 * Utility methods for launching partner shopping applications.
 */
object AppLauncherUtils {
    const val JD = "jing_dong"
    const val TAOBAO = "tao_bao"
    const val TMALL = "tian_mao"
    const val AMAZON = "amazon"
    const val PINDUODUO = "pin_duo_duo"

    private const val TAG = "Deeolink"

    private val shopData = hashMapOf(
        JD to ShopInfo(
            packageName = "com.jingdong.app.mall",
            url = "https://mall.jd.com/index-1000000127.html",
            displayNameRes = R.string.shop_name_jd
        ),
        TAOBAO to ShopInfo(
            packageName = "com.taobao.taobao",
            url = "https://apple.tmall.com/",
            displayNameRes = R.string.shop_name_taobao
        ),
        TMALL to ShopInfo(
            packageName = "com.tmall.wireless",
            url = "https://apple.tmall.com/",
            displayNameRes = R.string.shop_name_tmall
        ),
        AMAZON to ShopInfo(
            packageName = "com.amazon.mShop.android.shopping",
            url = " https://www.amazon.com/stores/Apple/page/77D9E1F7-0337-4282-9DB6-B6B8FB2DC98D",
            displayNameRes = R.string.shop_name_amazon
        ),
        PINDUODUO to ShopInfo(
            packageName = "com.xunmeng.pinduoduo",
            url = "https://mobile.yangkeduo.com/goods2.html?ps=z0yk81K35c",
            displayNameRes = R.string.shop_name_pdd
        )
    )

    private val shopPriorityOrder = mutableListOf(JD, TAOBAO, TMALL, AMAZON, PINDUODUO)

    /**
     * Returns the current priority order used by [openShop].
     */
    @JvmStatic
    fun priorityOrder(): List<String> = shopPriorityOrder.toList()

    /**
     * Register or override a shop configuration at runtime.
     */
    @JvmStatic
    fun registerShop(shopKey: String, shopInfo: ShopInfo) {
        shopData[shopKey] = shopInfo
        if (!shopPriorityOrder.contains(shopKey)) {
            shopPriorityOrder.add(shopKey)
        }
    }

    /**
     * Remove a shop configuration.
     */
    @JvmStatic
    fun removeShop(shopKey: String) {
        shopData.remove(shopKey)
        shopPriorityOrder.remove(shopKey)
    }

    /**
     * Replace the priority order used when [openShop] is called without parameters.
     */
    @JvmStatic
    fun setPriorityOrder(newOrder: List<String>) {
        shopPriorityOrder.clear()
        shopPriorityOrder.addAll(newOrder)
    }

    private fun checkApkExist(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openUrlByBrowser(context: Context, url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }.onFailure { error ->
            Log.e(TAG, "Unable to open browser for $url", error)
        }
    }

    @JvmStatic
    fun openShop(context: Context, shopKey: String) {
        shopData[shopKey]?.let { shopInfo ->
            openShop(context, shopInfo.url, shopInfo.packageName)
        } ?: run {
            Log.e(TAG, context.getString(R.string.open_shop_failed, shopKey))
        }
    }

    @JvmStatic
    fun openShop(context: Context) {
        for (shopKey in shopPriorityOrder) {
            shopData[shopKey]?.let { shopInfo ->
                if (openShop(context, shopInfo.url, shopInfo.packageName, false)) {
                    Log.d(TAG, context.getString(R.string.open_shop_priority, shopKey))
                    return
                }
            }
        }
        Log.e(TAG, context.getString(R.string.open_shop_no_available))
        Toast.makeText(context, context.getString(R.string.toast_no_available_shops), Toast.LENGTH_SHORT).show()
    }

    private fun openShop(
        context: Context,
        url: String,
        packageName: String,
        isShowToast: Boolean? = true
    ): Boolean {
        if (!checkApkExist(context, packageName)) {
            Log.e(TAG, context.getString(R.string.open_shop_package_missing, packageName))
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

        return runCatching {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = url.toUri()
                `package` = packageName
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                Log.e(TAG, context.getString(R.string.open_shop_intent_null, packageName))
                openUrlByBrowser(context, url)
                false
            }
        }.getOrElse { error ->
            Log.e(TAG, "Unable to open shop app for $packageName", error)
            openUrlByBrowser(context, url)
            false
        }
    }
}

data class ShopInfo(val packageName: String, val url: String, @StringRes val displayNameRes: Int)
