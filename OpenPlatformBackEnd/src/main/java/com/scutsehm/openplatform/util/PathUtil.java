package com.scutsehm.openplatform.util;

import java.io.File;

/**
 * 用于处理Path的工具类
 * 根据系统将path进行拼接
 * 区分linux风格的"/"和windows风格的"\\"
 */
public class PathUtil {
    public static String join(String pre, String next){
        String result = null;
        if(System.getProperty("os.name").contains("Windows")){
            result = pre + File.separator + next;
        }
        else if(System.getProperty("os.name").contains("Linux")){
            result = pre + File.separator + next;
        }
        return result;
    }
    public static String join(String pre, String mid, String next){
        String result = null;
        if(System.getProperty("os.name").contains("Windows")){
            result = pre + File.separator + mid + File.separator + next;
        }
        else if(System.getProperty("os.name").contains("Linux")){
            result = pre + File.separator + mid + File.separator + next;
        }
        return result;
    }

    //将路径中的分隔符替换成符合系统的
    public static String replaceSeparator(String path){
        String result = null;
        if(System.getProperty("os.name").contains("Windows")){

            result = path.replace("/",File.separator);
        }
        else if(System.getProperty("os.name").contains("Linux")){
            result = path.replaceAll("\\\\",File.separator);
        }
        return result;
    }


}
