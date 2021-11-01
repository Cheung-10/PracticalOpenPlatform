package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.Role;
import com.scutsehm.openplatform.POJO.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(long id);

    Long add(User user);

    boolean update(User user);

    Long deleteById(long id);

    List<Role> getRoleList();

    void setUserAdminRole(String username);
}
