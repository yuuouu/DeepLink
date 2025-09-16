#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, base64, hashlib, smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.header import Header
import argparse
import os
import csv
import json

parser = argparse.ArgumentParser()
parser.add_argument('--csv', required=True, help='CSV record data')
args = parser.parse_args()

csv_file = 'yuu.csv'

def main():
    header = ['执行时长', '编译时长', '上传时长', '开始时间', '执行情况', 
              'git信息', '推送信息', '失败原因', '传输信息']

    if not args.csv or args.csv.strip() == '""':
        print("Empty CSV record received")
        return

    try:
        from io import StringIO
        import csv
        
        f = StringIO(args.csv)
        print("scv ",f)
        reader = csv.reader(f)
        record = next(reader)  
        file_exists = os.path.isfile(csv_file)
        
        with open(csv_file, 'a', encoding='gbk', newline='') as f:
            writer = csv.writer(f)
            if not file_exists:
                writer.writerow(header)
            writer.writerow(record)
        
        print(f"Successfully {'created' if not file_exists else 'appended to'} {csv_file}")
    
    except Exception as e:
        print(f"Error processing CSV record: {str(e)}")
        raise

if __name__ == "__main__":
    main()
