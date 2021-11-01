package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.DTO.UserDTO;
import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.Role;
import com.scutsehm.openplatform.POJO.entity.User;
import com.scutsehm.openplatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理功能的接口
 */
@RestController
@Slf4j
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "findAll")
    public Result findAll(){
        List<User> list=new ArrayList<>();
        list=userService.findAll();

        //转换为DTO
        List<UserDTO> data=new ArrayList<>();
        for(User user:list){
            UserDTO userDTO= new UserDTO();
            BeanUtils.copyProperties(user,userDTO);
            List<String> tempRole=new ArrayList<>();
            for(Role role:user.getRoles()){
               tempRole.add(role.getRoleName());
            }
            userDTO.setRole(tempRole);
            data.add(userDTO);
        }
        return Result.OK().msg("success").data(data).build();

    }

    @GetMapping(value = "findById")
    public Result findById( Integer id){
        User user=new User();
        user=userService.findById(id);
        //转换为DTO
        UserDTO userDTO= new UserDTO();
        if(user!=null){
            BeanUtils.copyProperties(user,userDTO);
        }
        return Result.OK().msg("success").data(userDTO).build();
    }

    @PostMapping(value = "deleteById")
    public Result deleteById(Integer id){
        Long  count=userService.deleteById(id);
        return Result.OK().msg("success").data(null).build();
    }

    @PostMapping(value = "register")
    public Result register( User user){
        Long count=new Long(0);
        count=userService.add(user);
        return Result.OK().msg("注册用户成功").build();
    }

    @PostMapping(value = "update")
    public Result update(User user){
        boolean result=false;
        result=userService.update(user);
        if(!result){
            log.error("更新用户失败");
            return Result.BAD().msg("更新用户失败").build();
        }
        return Result.OK().msg("success").data(null).build();
    }


    @GetMapping(value = "getRoleList")
    public Result getRoleList(){
        List<Role> roleList = userService.getRoleList();
        return Result.OK().msg("success").data(roleList).build();
    }

    @PostMapping(value = "setUserAdminRole")
    public Result setUserAdminRole( String username){
        userService.setUserAdminRole(username);
        return Result.OK().msg("success").data(null).build();
    }
}
