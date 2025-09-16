# 电商平台的深度链接

优雅的调起 京东/淘宝/天猫/亚马逊/拼多多 app的深度链接 

**不需要申请任何权限**

## 原理

#### 1 逆向出apk的AndroidManifest.xml文件

搜索`scheme="https"`找到类似的内容
```xml
<intent-filter>
    <category android:name="android.intent.category.DEFAULT" />
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="http" />
    <data android:scheme="https" />
    <data android:host="*.abc.com" />
    <data android:host="*.qwe.com" />
</intent-filter>
```
#### 2 找到要进入的商铺或商品链接

在app内点击分享,复制链接,例如:https://mall.jd.com/index-1000000127.html

#### 3 接合内容

生成`ShopInfo(packageName = "com.jingdong.app.mall", url = "https://mall.jd.com/index-1000000127.html",
displayName = "京东")`

#### 4 注意在项目清单文件内需声明 queries

这样才能打开对应的app,例如: `<package android:name="com.jingdong.app.mall" />`