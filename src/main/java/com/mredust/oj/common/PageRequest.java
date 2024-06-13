package com.mredust.oj.common;

import com.mredust.oj.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当前页号
     */
    private long pageNum = 1;
    
    /**
     * 页面大小
     */
    private long pageSize = 10;
    
    
    /**
     * 排序字段
     */
    private String sortField;
    
    /**
     * 排序方式
     */
    private String sortOrder = CommonConstant.ASC;
}
