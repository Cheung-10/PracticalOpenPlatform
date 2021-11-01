package com.scutsehm.openplatform.POJO.DTO;


import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private long id;
    //用户名，具有唯一性
    private String username;
    //角色权限
    private List<String> role;
}
