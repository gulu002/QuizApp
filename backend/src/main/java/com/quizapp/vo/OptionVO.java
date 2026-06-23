package com.quizapp.vo;

import lombok.Data;

/**
 * 题目选项VO
 * <p>用于展示题目的单个选项信息</p>
 *
 * @author quizapp
 */
@Data
public class OptionVO {

    /** 选项标签，如 A、B、C、D */
    private String label;

    /** 选项文本内容 */
    private String text;
}