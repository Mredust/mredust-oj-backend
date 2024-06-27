package com.mredust.oj.judge;

import com.mredust.oj.model.entity.ProblemSubmit;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface JudgeService {
    
    ProblemSubmit handleJudge(long problemSubmitId);
}
