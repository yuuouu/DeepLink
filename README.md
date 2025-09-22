# Deeolink

一个轻量级的 Android Library，用于在无需额外权限的前提下，统一调起京东、淘宝、天猫、亚马逊、拼多多等常见电商平台的客户端。当对应 App 不存在时，会自动回退到浏览器打开相同页面。

## 快速集成

在 `build.gradle` 中添加依赖：

```kotlin
dependencies {
    implementation("io.github.yuuouu:deeolink:1.0.0")
}
```

> 该坐标通过 Maven Central 发布，模块源码位于本仓库的 `deeolink` 目录。

### Manifest 配置

Android 11 及以上版本需要显式声明可查询的包名，请在宿主 App 的 `AndroidManifest.xml` 中加入：

```xml
<queries>
    <package android:name="com.jingdong.app.mall" />
    <package android:name="com.taobao.taobao" />
    <package android:name="com.tmall.wireless" />
    <package android:name="com.amazon.mShop.android.shopping" />
    <package android:name="com.xunmeng.pinduoduo" />
</queries>
```

### 调用示例

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 指定平台
        AppLauncherUtils.openShop(this, AppLauncherUtils.TAOBAO)

        // 或按照默认优先级 (京东/淘宝/天猫/亚马逊/拼多多) 自动选择
        AppLauncherUtils.openShop(this)
    }
}
```

## 自定义扩展

`AppLauncherUtils` 内置了多个 `ShopInfo` 配置，包含包名、深度链接示例与展示名称。若需要新增或覆盖默认行为，可在应用初始化阶段调用：

```kotlin
val customShop = ShopInfo(
    packageName = "com.example.shop",
    url = "https://example.com/target",
    displayNameRes = R.string.shop_name_example
)

AppLauncherUtils.registerShop("custom_shop", customShop)
AppLauncherUtils.setPriorityOrder(listOf("custom_shop") + AppLauncherUtils.priorityOrder())
```

如不再需要某个平台，可使用 `AppLauncherUtils.removeShop("custom_shop")` 移除配置。

## 发布说明

- Group Id：`io.github.yuuouu`
- Artifact Id：`deeolink`
- Version：`1.0.0`
- 发布仓库：Maven Central（含 sources.jar）

如需本地调试发布流程，可执行：

```bash
./gradlew publishReleasePublicationToSonatypeRepository
```

在提供 PGP 签名信息与 Sonatype 账号后，即可将构件上传到 OSSRH 并同步到 Maven Central。
