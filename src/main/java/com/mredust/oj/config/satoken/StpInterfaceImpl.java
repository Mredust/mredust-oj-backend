package com.mredust.oj.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.mredust.oj.constant.UserConstant;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.user.RoleEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }
    
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<>();
        User user = (User) StpUtil.getSession().get(UserConstant.USER_LOGIN_KEY);
        if (RoleEnum.ADMIN.getCode().equals(user.getRole())) {
            list.add(RoleEnum.ADMIN.getValue());
        } else {
            list.add(RoleEnum.USER.getValue());
        }
        return list;
    }
}
