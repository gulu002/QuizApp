#!/usr/bin/env python
# -*- coding: utf-8 -*-
import pymysql

config = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'root',
    'database': 'quiz_app',
    'charset': 'utf8mb4'
}

conn = pymysql.connect(**config)
cursor = conn.cursor()

cursor.execute("SELECT COUNT(*) FROM question")
total = cursor.fetchone()[0]
print(f"总题目数: {total}")

cursor.execute("SELECT question_type, COUNT(*) FROM question GROUP BY question_type")
for row in cursor.fetchall():
    print(f"{row[0]}: {row[1]} 道")

cursor.execute("SELECT id, question_type, LEFT(title, 30), correct_answer FROM question LIMIT 5")
print("\n前5条记录:")
for row in cursor.fetchall():
    print(f"ID={row[0]}, 类型={row[1]}, 题干={row[2]}..., 答案={row[3]}")

cursor.close()
conn.close()
