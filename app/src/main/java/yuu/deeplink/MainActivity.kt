package yuu.deeplink

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_jd).setOnClickListener {
            AppLauncherUtils.openShop(this, "jd")
        }

        findViewById<Button>(R.id.btn_taobao).setOnClickListener {
            AppLauncherUtils.openShop(this, "taobao")
        }

        findViewById<Button>(R.id.btn_tmall).setOnClickListener {
            AppLauncherUtils.openShop(this, "tmall")
        }

        findViewById<Button>(R.id.btn_amazon).setOnClickListener {
            AppLauncherUtils.openShop(this, "amazon")
        }

        findViewById<Button>(R.id.btn_amazon_cn).setOnClickListener {
            AppLauncherUtils.openShop(this, "amazon_cn")
        }

        findViewById<Button>(R.id.btn_cycle).setOnClickListener {
            AppLauncherUtils.openShop(this)
        }
    }
}