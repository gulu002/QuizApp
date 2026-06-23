package com.quizapp.repository;

import com.quizapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类数据访问接口
 * <p>提供分类表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 根据关键词模糊搜索分类名称
     *
     * @param keyword 搜索关键词
     * @return 匹配的分类列表
     */
    List<Category> findByNameContaining(String keyword);

    /**
     * 根据父分类ID查找子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 查询所有分类，按排序序号升序排列
     *
     * @return 排序后的分类列表
     */
    List<Category> findAllByOrderBySortOrderAsc();
}