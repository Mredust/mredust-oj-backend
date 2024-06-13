package com.mredust.oj.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.model.dto.problem.*;
import com.mredust.oj.model.entity.Problem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.ProblemVO;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;

/**
* @author Mredust
* @description 针对表【problem(题库表)】的数据库操作Service
* @createDate 2024-06-06 13:27:50
*/
public interface ProblemService extends IService<Problem> {
    
    /**
     * 新增题目
     *
     * @param problemAddRequest 新增题目请求
     * @param userId         用户id
     * @return 新增的题目id
     */
    long addProblem(ProblemAddRequest problemAddRequest, long userId);
    
    /**
     * 更新题目
     *
     * @param problemUpdateRequest 更新题目请求
     * @return 是否更新成功
     */
    boolean updateProblem(ProblemUpdateRequest problemUpdateRequest);
    
    
    Page<ProblemVO> getProblemListByPage(ProblemQueryRequest problemQueryRequest,Long userId);
    
    
    ProblemVO objToVo(Problem problem, Long userId);
    
}
