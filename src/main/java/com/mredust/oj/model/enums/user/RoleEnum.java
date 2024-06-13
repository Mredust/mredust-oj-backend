package com.mredust.oj.model.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    USER("普通用户", "user", 0),
    
    ADMIN("管理员", "admin", 1);
    
    private final String text;
    
    private final String value;
    
    private final Integer code;
    
    public static RoleEnum getRoleEnumTypeByValue(String value) {
        return Stream.of(RoleEnum.values()).filter(roleEnum -> roleEnum.value.equals(value)).findFirst().orElse(null);
    }
    
    public static RoleEnum getRoleEnumTypeByCode(Integer code) {
        return Stream.of(RoleEnum.values()).filter(roleEnum -> roleEnum.code.equals(code)).findFirst().orElse(null);
    }
    
}
