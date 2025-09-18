# 电商平台深度链接示例

仓库提供了一个 Android 示例应用，展示如何在不申请额外权限的情况下，从应用内一键跳转到京东、淘宝、天猫、亚马逊、拼多多等主流电商平台的指定页面。  

## Jenkins相关

保姆级教程: [从零搭建 Jenkins Android 自动发包体系](https://yuuou.vercel.app/%E4%B8%9A%E5%8A%A1%E6%80%9D%E8%80%83/%E4%BB%8E%E9%9B%B6%E6%90%AD%E5%BB%BA%20Jenkins%20Android%20%E8%87%AA%E5%8A%A8%E5%8F%91%E5%8C%85%E4%BD%93%E7%B3%BB)  

具体代码可查看 [pipeline](./jenkins/pipeline)

## 功能亮点

- 📦 **内置多家电商配置**：预置多个平台的包名与示例链接，可直接体验或替换为自己的链接。
- 🚀 **统一调起能力**：根据已安装的 App 自动选择最优平台，若未安装则回退到浏览器打开 H5 页面。
- 🔒 **无需额外权限**：通过深度链接协议调起，无需在运行时申请敏感权限。
- 🧩 **易于扩展**：仅需配置 `packageName` 与目标 URL，即可新增或替换商家入口。

## 快速开始

1. **克隆项目并导入**
   - 使用 `git clone` 下载仓库，或直接在 Android Studio 中选择 *Get from VCS*。
   - 使用 Android Studio 打开项目根目录后，会自动完成 Gradle 同步。

2. **运行示例应用**
   - 连接一台已安装目标电商平台的 Android 设备或启动模拟器。
   - 编译并运行 `app` 模块，在首页选择对应平台即可验证调起效果。

3. **自定义店铺或商品链接**
   - 打开 [`AppLauncherUtils.kt`](app/src/main/java/yuu/deeplink/AppLauncherUtils.kt)，修改 `shopData` 中各平台的 `url` 字段即可替换跳转目标。
   - 如需新增平台，可新增一条 `ShopInfo` 配置，并在 `SHOP_PRIORITY_ORDER` 中指定优先级。

## 实现原理

深度链接调起流程主要包含以下步骤：

1. **解析第三方 App 的入口配置**：
   - 通过反编译目标 App，阅读 `AndroidManifest.xml`，定位 `<intent-filter>` 中的深度链接规则（常见为 `http/https` scheme）。
2. **准备目标链接**：
   - 在目标 App 内点击分享或复制链接，确认其与 Manifest 中的规则匹配，例如 `https://mall.jd.com/index-1000000127.html`。
3. **封装跳转逻辑**：
   - 在项目中创建 `ShopInfo(packageName, url, displayName)`，并根据用户选择构造 `Intent.ACTION_VIEW` 去调起对应 App。
4. **适配 App 安装状态**：
   - 在尝试启动前先检测包是否存在，未安装时回退到系统浏览器打开同一链接，保证流程可用性。

## 必要的清单配置

为确保应用能够发现并调用其他 App，需要在主模块的 `AndroidManifest.xml` 中声明 `queries`：

```xml
<queries>
    <package android:name="com.jingdong.app.mall" />
    <package android:name="com.taobao.taobao" />
    <package android:name="com.tmall.wireless" />
    <package android:name="com.amazon.mShop.android.shopping" />
    <package android:name="com.xunmeng.pinduoduo" />
</queries>
```

如果新增或修改平台，记得同步更新清单配置，避免在 Android 11 及以上版本无法查询到目标 App。

## 常见问题

- **为什么没有反应？** 请确认目标 App 是否安装，或查看 Logcat 是否有 `open_shop` 相关日志。
- **如何扩展到更多平台？** 拿到目标 App 的包名与可匹配的深度链接，按照上文的方式在 `shopData` 中补充即可。
- **能否在 WebView 中使用？** 只要能够触发上述跳转逻辑，即可复用；注意在 WebView 环境下同样需要检查设备是否安装目标 App。