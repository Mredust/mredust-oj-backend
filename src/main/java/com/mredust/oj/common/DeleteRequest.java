package com.mredust.oj.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    @NotNull
    @Positive
    @ApiModelProperty(value = "id")
    private Long id;
}

