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
            AppLauncherUtils.openShop(this, AppLauncherUtils.jd)
        }

        findViewById<Button>(R.id.btn_taobao).setOnClickListener {
            AppLauncherUtils.openShop(this, AppLauncherUtils.tb)
        }

        findViewById<Button>(R.id.btn_tmall).setOnClickListener {
            AppLauncherUtils.openShop(this, AppLauncherUtils.tm)
        }

        findViewById<Button>(R.id.btn_amazon).setOnClickListener {
            AppLauncherUtils.openShop(this, AppLauncherUtils.amazon)
        }

        findViewById<Button>(R.id.btn_pdd).setOnClickListener {
            AppLauncherUtils.openShop(this, AppLauncherUtils.pdd)
        }

        findViewById<Button>(R.id.btn_cycle).setOnClickListener {
            AppLauncherUtils.openShop(this)
        }
    }
}