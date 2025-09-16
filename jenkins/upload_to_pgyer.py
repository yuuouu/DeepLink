import os
import time
import json
import requests
import argparse

# 🔐 固定 Key 配置（可配置在此处）
PGY_API_KEY = ""

parser = argparse.ArgumentParser()
parser.add_argument('--file', required=True, help='APK 文件路径')
parser.add_argument('--install_type', default='1', help='安装类型')
parser.add_argument('--password', default='', help='安装密码')
parser.add_argument('--update_description', default='更新~', help='更新说明')

args = parser.parse_args()

APK_PATH = args.file
installType = args.install_type
password = args.password
updateDescription = args.update_description

# 忽略证书警告
requests.packages.urllib3.disable_warnings()

def get_cos_token(install_type, password, update_description):
    url = "https://api.pgyer.com/apiv2/app/getCOSToken"
    data = {
        "_api_key": PGY_API_KEY,
        "buildType": "apk",
        "buildUpdateDescription": update_description,
        "buildInstallType": install_type,
        "buildPassword": password,
    }
    res = requests.post(url, data=data)
    res.raise_for_status()
    result = res.json()
    return result["data"]

def upload_to_cos(cos_data):
    files = {"file": open(APK_PATH, "rb"),}
    payload = cos_data["params"]
    payload["key"] = cos_data["key"]
    payload["x-cos-meta-file-name"] = os.path.basename(APK_PATH)
    response = requests.post(cos_data["endpoint"], data=payload, files=files, verify=False)
    response.raise_for_status()
    return cos_data["key"]

def complete_upload_form(build_key, install_type, password, update_description):
    url = "https://api.pgyer.com/apiv2/app/uploadComplete"
    payload = {
        "_api_key": PGY_API_KEY,
        "buildKey": build_key,
        "buildUpdateDescription": update_description,
        "buildInstallType": install_type,
        "buildPassword": password,
    }

    print(f"updateDescription: {updateDescription}  === {update_description}")
    # ✅ 打印实际上传参数
    print("📝 正在提交以下安装信息：")
    for k, v in payload.items():
        print(f"  {k}: {v}")

    response = requests.post(url, data=payload, verify=False)
    response.raise_for_status()
    return response.json()

def wait_for_publish(build_key, max_wait_seconds=300, interval=5):
    """
    等待应用发布成功，最大等待时间为 max_wait_seconds 秒，每 interval 秒轮询一次
    """
    url = "https://api.pgyer.com/apiv2/app/buildInfo"
    params = {
        "_api_key": PGY_API_KEY,
        "buildKey": build_key
    }
    max_retries = max_wait_seconds // interval

    for i in range(max_retries):
        response = requests.get(url, params=params, verify=False)
        data = response.json()

        if data.get("code") == 0:
            print("✅ 发布成功！")
            return data
        elif data.get("code") == 1216:
            print("❌ 发布失败:", data.get("message"))
            return data
        elif data.get("code") == 1247:
            print(f"⏳ 第 {i + 1}/{max_retries} 次轮询：App 正在发布中，请稍候...")
            time.sleep(interval)
        else:
            print(f"⚠️ 其他返回：{data}")
            time.sleep(interval)

    # 超时
    return {
        "code": -1,
        "message": "发布超时（5分钟未完成）"
    }

def upload_app_to_pgyer(install_type, password, update_description, output_json="upload_result.json"):
    print("🚀 获取上传地址...")
    cos_token = get_cos_token(install_type, password, update_description)
    print(f"{cos_token}")

    print("📦 上传 APK 文件... ")
    build_key = upload_to_cos(cos_token)

    print(f"{build_key}")
    print("🔄 等待发布结果...")
    result = wait_for_publish(build_key)

    print(f"✅ 完成，结果保存至：{output_json}")
    with open(output_json, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=4)

if __name__ == "__main__":
   print("📦 环境变量读取结果：")
   print(f"APK_PATH: {APK_PATH}")
   print(f"installType: {installType}")
   print(f"password: {password}")
   print(f"updateDescription: {updateDescription}")
   upload_app_to_pgyer(installType, password, updateDescription)