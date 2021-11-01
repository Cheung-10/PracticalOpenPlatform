package com.scutsehm.openplatform.POJO.enums;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.scutsehm.openplatform.POJO.enums.FileSpace.*;

/**
 * 用户操作文件的动作权限；
 * 各动作对应一个可操控的文件空间的列表；
 */
public enum Operation {
    /** 修改/写入文件的权限 */
    Write(new FileSpace[]{PRIVATE, SHARE }),
    /** 查看/读取文件的权限 */
    Read(new FileSpace[]{PRIVATE, SHARE, DATA}),
    /** 管理员权限 */
    Admin(new FileSpace[]{PRIVATE, SHARE, DATA, TRAINMODEL, PROCESSMODEL}),
    /** 空权限 */
    None(),

    /** 拷贝文件到此处的权限 */
    CopyTo(Write),
    /** 上传文件的权限 */
    UpLoad(Write),
    /** 创建新文件夹的权限 */
    MakeDir(Write),
    /** 解压缩的权限 */
    UnZip(Write),
    /** 删除文件的权限 */
    Delete(Write),

    /** 下载文件的权限 */
    DownLoad(Read),
    /** 获取目录文件的权限 */
    GetFileList(Read),
    /** 从此处拷贝文件的权限 */
    CopyFrom(Read),

    /** 作为模型输入路径的权限 */
    ModelInput(Read),
    /** 作为模型输出路径的权限 */
    ModelOutput(Write),
    /** 调用训练模型的权限 */
    CallTrainModel(new FileSpace[]{TRAINMODEL}),
    /** 调用处理模型的权限 */
    CallProcessModel(new FileSpace[]{PROCESSMODEL}),
    /** 发布处理模型 */
    PublishProcessModel(new FileSpace[]{PROCESSMODEL}),
    /** 发布训练模型 */
    PublishTrainModel(new FileSpace[]{TRAINMODEL})
    ;

    private Set<FileSpace> usableSpace = new HashSet<>();

    Operation(FileSpace[] spaces){
        Collections.addAll(this.usableSpace, spaces);
    }
    Operation(Operation op){
        usableSpace = op.usableSpace;
    }
    Operation(){}

    public Operation addSpace(FileSpace space){
        usableSpace.add(space);
        return this;
    }
    public Operation addSpace(FileSpace[] spaces){
        Collections.addAll(this.usableSpace, spaces);
        return this;
    }

    public boolean hasSpace(FileSpace space){
        return this.usableSpace.contains(space);
    }
}
