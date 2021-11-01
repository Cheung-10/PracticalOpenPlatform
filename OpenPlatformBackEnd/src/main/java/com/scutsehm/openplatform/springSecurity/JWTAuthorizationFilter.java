package com.scutsehm.openplatform.springSecurity;

import com.scutsehm.openplatform.util.JwtTokenUtils;
import com.scutsehm.openplatform.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode.*;

/**
 该过滤器用于用户权限认证
 */

public class JWTAuthorizationFilter  extends BasicAuthenticationFilter {


    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader((JwtTokenUtils.HEADER));
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);

            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        Claims claims=null;
        try{
            claims = JwtTokenUtils.getTokenBody(token);
        }catch (Exception e){
            //JWT 解析错误,无权访问
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("status",ACCESSFAIL.getCode());
            resultMap.put("msg",ACCESSFAIL.getDescription());
            Map<String,Object> data=new HashMap<>();
            resultMap.put("data",data);
            ResponseUtil.out(response,resultMap);
            return;
        }

        //判断JWT是否过时
        if (claims.getExpiration().getTime() <= new Date().getTime()) { // Token超时
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("status",JWTEXPIRED.getCode());
            resultMap.put("msg",JWTEXPIRED.getDescription());
            Map<String,Object> data=new HashMap<>();
            resultMap.put("data",data);
            ResponseUtil.out(response,resultMap);
            return;
        }
        String username = JwtTokenUtils.getUsername(token);
        List<String> userRole = JwtTokenUtils.getUserRole(token);
        // 获取用户角色
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        for(String role:userRole){
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UsernamePasswordAuthenticationToken newToken;
        if (username != null) {
            newToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        } else {
            newToken = null;
        }
        SecurityContextHolder.getContext().setAuthentication(newToken);
        super.doFilterInternal(request, response, chain);
    }
}

