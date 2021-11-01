package com.scutsehm.openplatform.service.impl;

import com.mysql.cj.util.StringUtils;
import com.scutsehm.openplatform.POJO.entity.Role;
import com.scutsehm.openplatform.POJO.entity.User;
import com.scutsehm.openplatform.exception.BusinessException;
import com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode;
import com.scutsehm.openplatform.dao.mapper.RoleMapper;
import com.scutsehm.openplatform.dao.mapper.UserMapper;
import com.scutsehm.openplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);

        if(user==null) {
            throw new UsernameNotFoundException(username);
        }else{
            List<Role> userRoles = userMapper.getUserRoleByUserId(user.getId());
            user.setRoles(userRoles);
        }
        return user;
    }


    @Override
    public List<User> findAll() {
        List<User> list=userMapper.findAll();
        for (User user:list) {
            user.setRoles(userMapper.getUserRoleByUserId(user.getId()));
        }
        return list;
    }

    @Override
    public User findById(long id) {
        User user=userMapper.findById(id);
        return user;
    }

    @Override
    public Long add(User user) {
        //检查账号用户名是否合法，需要全部为数字
        Pattern pattern = Pattern.compile("^-?[0-9]+");
        Matcher isNum = pattern.matcher(user.getUsername());
        if(!isNum.matches()){
            throw new BusinessException(BusinessErrorCode.USERNAMEERROR);
        }
        //先检查账号是否存在,若存在则抛出异常
        User oldUser = userMapper.findByUsername(user.getUsername());
        if(oldUser!=null){
            throw new BusinessException(BusinessErrorCode.USEREXISTED);
        }

        //需要将新用户的密码通过BCrypt的方式加密后储存
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword().trim());
        user.setPassword(encodedPassword);
        Long result=userMapper.add(user);

        // 添加新用户时默认加上用户的角色属性
        Role userRole=roleMapper.findRoleByRoleName("ROLE_USER");
        roleMapper.setUserRole(user.getId(),userRole.getId());
        return result;
    }

    @Override
    public boolean update(User user) {
        //需要将密码通过BCrypt的方式加密后储存
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword().trim());
            user.setPassword(encodedPassword);
        }
        boolean result=userMapper.update(user);
        return result;
    }

    @Override
    public Long deleteById(long id) {
        Long result=userMapper.deleteById(id);
        return result;
    }

    @Override
    public List<Role> getRoleList() {
        List<Role> roleList = roleMapper.findAll();
        return roleList;
    }

    @Override
    public void setUserAdminRole(String username) {
        User user = userMapper.findByUsername(username);
        Role role_admin = roleMapper.findRoleByRoleName("ROLE_ADMIN");
        roleMapper.setUserRole(user.getId(), role_admin.getId());

    }


}
