package yuu.deeplink

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity: AppCompatActivity() {
    private val shopData = hashMapOf(
        "jd" to ShopInfo(
            packageName = "com.jingdong.app.mall", url = "https://mall.jd.com/index-11437885.html", displayName = "京东"
        ),
        "taobao" to ShopInfo(
            packageName = "com.taobao.taobao", url = "https://obsbot.tmall.com/", displayName = "淘宝"
        ),
        "tmall" to ShopInfo(
            packageName = "com.tmall.wireless", url = "https://obsbot.tmall.com/", displayName = "天猫"
        ),
        "amazon" to ShopInfo(
            packageName = "com.amazon.mShop.android.shopping", url = "https://www.amazon.com/s?me=A3NN5GYI68DJ8", displayName = "亚马逊"
        ),
        "amazon_cn" to ShopInfo(
            packageName = "cn.amazon.mShop.android", url = "https://globalstore.amazon.cn/brandStore/OBSBOT", displayName = "亚马逊中国"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_jd).setOnClickListener {
            openShop("jd")
        }

        findViewById<Button>(R.id.btn_taobao).setOnClickListener {
            openShop("taobao")
        }

        findViewById<Button>(R.id.btn_tmall).setOnClickListener {
            openShop("tmall")
        }

        findViewById<Button>(R.id.btn_amazon).setOnClickListener {
            openShop("amazon")
        }

        findViewById<Button>(R.id.btn_amazon_cn).setOnClickListener {
            openShop("amazon_cn")
        }

        findViewById<Button>(R.id.btn_cycle).setOnClickListener {
            AppLauncherUtils.openShop(this, shopData)
        }
    }

    private fun openShop(shopKey: String) {
        shopData[shopKey]?.let { shopInfo ->
            AppLauncherUtils.openShop(this, shopInfo.url, shopInfo.packageName)
        } ?: run {
            Toast.makeText(this, "未找到对应的应用信息", Toast.LENGTH_SHORT).show()
        }
    }

}