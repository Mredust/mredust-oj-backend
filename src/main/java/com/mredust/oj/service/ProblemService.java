package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.dto.problem.ProblemAddRequest;
import com.mredust.oj.model.dto.problem.ProblemQueryRequest;
import com.mredust.oj.model.dto.problem.ProblemUpdateRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.vo.ProblemVO;

/**
 * @author Mredust
 * @description 针对表【problem(题库表)】的数据库操作Service
 * @createDate 2024-06-06 13:27:50
 */
public interface ProblemService extends IService<Problem> {
    
    
    long addProblem(ProblemAddRequest problemAddRequest);
    
    boolean updateProblem(ProblemUpdateRequest problemUpdateRequest);
    
    
    
    Page<ProblemVO> getProblemListByPage(ProblemQueryRequest problemQueryRequest);
    
    
    ProblemVO objToVo(Problem problem);
    
    Page<ProblemVO> getProblemListVoByPage(ProblemQueryRequest problemQueryRequest);
}
