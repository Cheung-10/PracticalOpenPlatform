package com.scutsehm.openplatform.util;

import com.scutsehm.openplatform.POJO.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

import java.util.*;


/**
 * 用于处理JWT相关的操作
 * 主要存储JWT属性信息、生成JWT token 和解析JWT token
 */

@Data
public class JwtTokenUtils  {
    /** Request Headers ： Authorization */
    public  static final String HEADER="Authorization";

    /** 令牌前缀，最后留个空格 Bearer */
    public  static final String  TOKEN_PREFIX = "Bearer ";

    /** Base64对该令牌进行编码 */
    public  static final String BASE64SECRET="+zsz3JdZReJ4WALiaxjL/g==";

    /** 令牌过期时间 3600秒 即一小时*/
    public  static final Long EXPIRATION=3600L;

    /** 记住我的令牌过期时间  七天*/
    public  static final Long EXPIRATIONREMEMBER=604800L;


    // 添加角色的key
    public  final static String ROLE_CLAIMS = "rol";

    // 创建token
    public   static String createToken(String username, boolean isRememberMe, List<Role> roles) {
        long expirationTime = isRememberMe ? EXPIRATIONREMEMBER : EXPIRATION;
        HashMap<String, Object> map = new HashMap<>();
        List<String> roleList=new ArrayList<>();
        for (Role role:roles){
            roleList.add(role.getRoleName());
        }
        map.put(ROLE_CLAIMS, roleList);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, BASE64SECRET)
                // 这里要早set一点，放到后面会覆盖别的字段
                .setClaims(map)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .compact();
    }

    // 从token中获取用户名
    public  static  String getUsername(String token){
        return getTokenBody(token).getSubject();
    }

    // 是否已过期
    public   static long getExpiration(String token){
        return getTokenBody(token).getExpiration().getTime();
    }

    // 从token中获取角色名
    public   static List<String> getUserRole(String token){
        Claims claims=getTokenBody(token);
        List<String> roles=(List<String>)claims.get(ROLE_CLAIMS);
        return roles;
    }

    public  static Claims getTokenBody(String token){
        Claims claims;
        try{
            claims=Jwts.parser()
                    .setSigningKey(BASE64SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            claims=e.getClaims();
        }
        return claims;
    }


}


