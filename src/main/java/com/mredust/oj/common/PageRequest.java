package com.mredust.oj.common;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "当前页码")
    private long pageNum = 1;
    
    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    private long pageSize = 10;
}
