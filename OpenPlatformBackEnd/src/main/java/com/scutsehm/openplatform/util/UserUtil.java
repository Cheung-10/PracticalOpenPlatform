package com.scutsehm.openplatform.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 解析用户属性的工具类
 */
public class UserUtil {

    //获得用户名
    public static  String getUsername(){
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return username;
//            return "developer";
    }

    public static String getUserPath(){
        return "/" + getUsername();
    }

    //获得用户角色
    public static List<String> getRole(){
        List<String> userRole=new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for(GrantedAuthority authority:authorities){
            userRole.add( authority.getAuthority());

        }
        return userRole;
    }

    //判断用户是否为管理员
    public static boolean isAdmin(){
        List<String> userRole=getRole();
        if(userRole.contains("ROLE_ADMIN")){
            return true;
        }
        return false;
//        return true;
    }
}
