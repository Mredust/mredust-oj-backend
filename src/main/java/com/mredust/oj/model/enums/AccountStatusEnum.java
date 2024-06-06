package com.mredust.oj.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum AccountStatusEnum {
    
    NORMAL("正常", 0),
    BAN("封号", 1);
    
    private final String status;
    
    private final Integer code;
    
    public static AccountStatusEnum getAccountStatusEnumTypeByCode(Integer code) {
        return Stream.of(AccountStatusEnum.values()).filter(accountStatusEnum -> accountStatusEnum.code.equals(code)).findFirst().orElse(null);
    }
    
}
