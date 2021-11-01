package com.scutsehm.openplatform.springSecurity;

import com.scutsehm.openplatform.POJO.entity.Role;
import com.scutsehm.openplatform.POJO.entity.User;

import com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode;
import com.scutsehm.openplatform.util.JwtTokenUtils;
import com.scutsehm.openplatform.util.ResponseUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
该过滤器用于用户账号的认证
 基于认证失败和认证成功有相应的返回
 */


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl("/user/login");

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {

        // 从输入流中获取到登录的信息
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>())
        );

    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // 查看源代码会发现调用getPrincipal()方法会返回一个实现了`UserDetails`接口的对象
        // 所以就是JwtUser啦
        User user = (User) authResult.getPrincipal();
        boolean isRememberMe=Boolean.parseBoolean(request.getParameter("isRememberMe"));
        List<Role> roles = user.getRoles();
        long  currTime = new Date().getTime();
        String token = JwtTokenUtils.createToken(user.getUsername(), isRememberMe,roles);
        long endTime=JwtTokenUtils.getExpiration(token);
        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的格式应该是 `Bearer token`
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("status",200);
        resultMap.put("msg","认证成功");
        Map<String,Object> data=new HashMap<>();
        data.put("token",JwtTokenUtils.TOKEN_PREFIX + token);
        data.put("id",user.getId());
        resultMap.put("data",data);
        resultMap.put("assTime",currTime);
        resultMap.put("endTime",endTime);
        ResponseUtil.out(response,resultMap);
    }

    //认证失败调用的方法
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("status", BusinessErrorCode.LOGINFAIL.getCode());
        resultMap.put("msg",BusinessErrorCode.LOGINFAIL.getDescription());
        Map<String,Object> data=new HashMap<>();
        resultMap.put("data",data);
        ResponseUtil.out(response,resultMap);
    }


}
