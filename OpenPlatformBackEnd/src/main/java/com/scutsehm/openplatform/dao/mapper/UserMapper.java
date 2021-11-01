package com.scutsehm.openplatform.dao.mapper;

import com.scutsehm.openplatform.POJO.entity.Role;
import com.scutsehm.openplatform.POJO.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();

    User findByUsername(String username);

    User findById(long id);

    Long add(User user);

    boolean update(User user);

    Long deleteById(long id);

    List<Role> getUserRoleByUserId(long userId);


}
