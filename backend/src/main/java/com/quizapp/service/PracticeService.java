package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.dto.FinishSessionDTO;
import com.quizapp.dto.PracticeStartDTO;
import com.quizapp.dto.SubmitAnswerDTO;
import com.quizapp.vo.AnswerResultVO;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.PracticeSessionVO;
import com.quizapp.vo.QuestionVO;

import java.util.List;

/**
 * 练习服务接口
 * <p>提供开始练习、提交答案、结束练习、查看进度和历史等业务操作</p>
 *
 * @author quizapp
 */
public interface PracticeService {

    /**
     * 开始练习，创建练习会话并抽取题目
     *
     * @param userId 用户ID
     * @param dto    练习参数（类型、题型、分类、数量等）
     * @return 练习会话信息
     */
    Result<PracticeSessionVO> startPractice(Long userId, PracticeStartDTO dto);

    /**
     * 提交答案，记录答题结果并更新错题本和掌握度
     *
     * @param userId 用户ID
     * @param dto    答题参数（会话ID、题目ID、用户答案、耗时）
     * @return 答题结果（是否正确、正确答案、解析、是否掌握）
     */
    Result<AnswerResultVO> submitAnswer(Long userId, SubmitAnswerDTO dto);

    /**
     * 结束练习会话
     *
     * @param userId 用户ID
     * @param dto    结束参数（会话ID）
     * @return 练习会话最终信息
     */
    Result<PracticeSessionVO> finishPractice(Long userId, FinishSessionDTO dto);

    /**
     * 获取练习会话进度
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 会话进度信息
     */
    Result<PracticeSessionVO> getSessionProgress(Long userId, String sessionId);

    /**
     * 分页查询练习历史记录
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页会话列表
     */
    Result<PageVO<PracticeSessionVO>> getSessionHistory(Long userId, int page, int size);

    /**
     * 获取练习会话中的所有题目
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 题目列表
     */
    Result<List<QuestionVO>> getSessionQuestions(Long userId, String sessionId);
}