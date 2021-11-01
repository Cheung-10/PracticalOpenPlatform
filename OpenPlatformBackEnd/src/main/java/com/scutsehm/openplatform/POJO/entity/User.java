package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {

  //用户id
  private long id;
  //用户名，具有唯一性
  private String username;
  //用户密码
  private String password;
  //用户对应的角色
  private List<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (roles == null) {
      return null;
    }
    // 将自定义的Role转换为Security的GrantedAuthority
    List<SimpleGrantedAuthority> authorities=new ArrayList<>();
    for(Role role:roles){
      authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
    }
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
