package com.scutsehm.openplatform.util;

import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.POJO.enums.Operation;

/** 用以检查文件访问权限，
 * 用例：{@code FileAccessUtil.canAccess(Operation.READ, FileSpace)}，
 * Operation是当前将执行的操作，FileSpace是将操作的文件空间
 */
public class FileAccessUtil {
    public static boolean canAccess(Operation op, String fileSpace){
        FileSpace space = FileSpace.valueOf(fileSpace.toUpperCase());
        return op.hasSpace(space);
    }
    public static boolean canAccess(Operation op, FileSpace fileSpace){
        return op.hasSpace(fileSpace);
    }
}
