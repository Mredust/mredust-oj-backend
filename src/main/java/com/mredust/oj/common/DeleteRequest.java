package com.mredust.oj.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
}

