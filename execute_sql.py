#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
执行 init_questions.sql 插入数据库
"""

import pymysql

# 数据库连接配置
config = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'root',
    'database': 'quiz_app',
    'charset': 'utf8mb4'
}

def execute_sql_file(sql_file):
    # 读取 SQL 文件
    with open(sql_file, 'r', encoding='utf-8') as f:
        sql_content = f.read()

    # 连接数据库
    conn = pymysql.connect(**config)
    cursor = conn.cursor()

    try:
        # 按分号分割 SQL 语句
        statements = sql_content.split(';')
        count = 0
        for stmt in statements:
            stmt = stmt.strip()
            if stmt and stmt.startswith('INSERT'):
                cursor.execute(stmt)
                count += 1
                if count % 100 == 0:
                    print(f"已插入 {count} 条记录...")

        conn.commit()
        print(f"完成！共插入 {count} 条记录")

    except Exception as e:
        conn.rollback()
        print(f"错误：{e}")
        raise
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    execute_sql_file(r'g:\QuizApp\backend\src\main\resources\init_questions.sql')
