#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
解析 quiz_bank.md 题库文件，生成 question 表的 INSERT SQL 语句
"""

import re
import json

def parse_quiz_bank(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # 按题目分割（### 题号.【题型】）
    pattern = re.compile(
        r'###\s*(\d+)\.\s*【(多选题|选择题|单选题|判断题)】\s*\n+'
        r'\s*\*\*(.+?)\*\*\s*\n+'   # 题干（加粗）
        r'\s*'                        # 空行
        r'((?:-\s*[A-E]、[^\n]*\n?)+)',  # 选项列表（单行匹配）
        re.DOTALL
    )

    questions = []
    for m in pattern.finditer(content):
        source_number = int(m.group(1))
        q_type_str = m.group(2)
        title = m.group(3).strip()
        options_text = m.group(4).strip()

        # 映射题型
        type_map = {'多选题': 'MULTI', '选择题': 'SINGLE', '单选题': 'SINGLE', '判断题': 'JUDGE'}
        question_type = type_map.get(q_type_str, 'MULTI')

        # 解析选项
        options = []
        correct_answers = []
        opt_pattern = re.compile(r'-\s*([A-E])、(.+)')
        for line in options_text.split('\n'):
            line = line.strip()
            if not line:
                continue
            om = opt_pattern.match(line)
            if om:
                label = om.group(1)
                text = om.group(2).strip()
                # 检查是否标注答案
                is_answer = ' **【答案】**' in text
                if is_answer:
                    text = text.replace(' **【答案】**', '').strip()
                    correct_answers.append(label)
                options.append({'label': label, 'text': text})

        # 正确答案排序
        correct_answers.sort()
        correct_answer = ''.join(correct_answers)

        # 生成 options_json
        options_json = json.dumps(options, ensure_ascii=False)

        # 转义单引号
        title_escaped = title.replace("'", "''")
        options_json_escaped = options_json.replace("'", "''")
        correct_answer_escaped = correct_answer.replace("'", "''")

        sql = (
            f"INSERT INTO question (category_id, question_type, title, options_json, "
            f"correct_answer, explanation, difficulty, source_number, status, create_time, update_time) "
            f"VALUES (NULL, '{question_type}', '{title_escaped}', '{options_json_escaped}', "
            f"'{correct_answer_escaped}', NULL, 2, {source_number}, 1, NOW(), NOW());"
        )
        questions.append(sql)

    # 写入 SQL 文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- 题库数据 INSERT 语句\n")
        f.write(f"-- 共 {len(questions)} 道题\n")
        f.write(f"-- 生成日期：自动生成\n\n")
        f.write("SET NAMES utf8mb4;\n\n")
        for q in questions:
            f.write(q + '\n')

    print(f"解析完成：共 {len(questions)} 道题")
    print(f"输出文件：{output_file}")

if __name__ == '__main__':
    parse_quiz_bank(
        r'g:\QuizApp\quiz_bank.md',
        r'g:\QuizApp\backend\src\main\resources\init_questions.sql'
    )
