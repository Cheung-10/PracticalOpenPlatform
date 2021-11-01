package com.scutsehm.openplatform.springSecurity;

import com.scutsehm.openplatform.util.ResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("status",403);
        resultMap.put("msg","无权访问");
        Map<String,Object> data=new HashMap<>();
        resultMap.put("data",data);
        ResponseUtil.out(httpServletResponse,resultMap);
    }
}
