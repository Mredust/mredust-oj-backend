package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.ProblemSubmitVO;

/**
 * @author Mredust
 * @description 针对表【problem_submit(题目提交表)】的数据库操作Service
 * @createDate 2024-06-06 13:27:55
 */
public interface ProblemSubmitService extends IService<ProblemSubmit> {
    
    ProblemSubmitVO problemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser);
    
    ProblemSubmitVO getProblemSubmitVoById(Long id);
}
