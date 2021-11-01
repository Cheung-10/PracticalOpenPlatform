package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.POJO.enums.Operation;
import com.scutsehm.openplatform.config.FilePathConfig;
import com.scutsehm.openplatform.util.FileAccessUtil;
import com.scutsehm.openplatform.util.FileAndPathUtils;
import org.springframework.web.bind.annotation.RequestMapping;

public class TestController {
//    private static FilePathConfig filePathConfig;

    @RequestMapping("/test")
    public void shili(){
        String path = "/aaa";

        String result = FileAndPathUtils.getAbsolutePath("Private", "/test");
        System.out.println(result);
    }
}
