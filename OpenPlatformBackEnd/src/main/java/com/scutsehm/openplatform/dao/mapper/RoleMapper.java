package com.scutsehm.openplatform.dao.mapper;

import com.scutsehm.openplatform.POJO.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    Role findRoleByRoleName(String roleName);

    Long setUserRole(long userId,long roleId);

    List<Role> findAll();
}
