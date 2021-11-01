package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.DTO.FileDTO;
import com.scutsehm.openplatform.POJO.DTO.FileListDTO;
import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.exception.BusinessException;
import com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode;
import com.scutsehm.openplatform.service.FileSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 处理平台文件的接口；
 * TODO 目前未对用户是否具有文件操作权限执行检查
 */
@RestController
@Slf4j
@RequestMapping("fileSpace")
public class FileSpaceController {


    @Autowired
    private FileSpaceService fileSpaceService;

    @PostMapping(value = "getFiles")
    public Result getFiles(String space, String path){

        List<FileDTO> fileDTOS = fileSpaceService.getFiles(space, path);
        FileListDTO fileListDTO=new FileListDTO();
        fileListDTO.setFileDTOList(fileDTOS);
        fileListDTO.setPath(path);
        return Result.OK().msg("success").data(fileListDTO).build();
    }

    @PostMapping(value = "makeDir")
    public Result makeDir(String space, String path,String dirName){
        boolean result=fileSpaceService.makeDir(space, path, dirName);
        if(result){
            return Result.OK().msg("success").data(null).build();
        }else{
            throw new BusinessException(BusinessErrorCode.FILEEXISTED);
        }
    }


    @PostMapping(value = "uploadFile")
    public Result uploadFile(String space, String path, MultipartFile file) throws IOException {
        fileSpaceService.uploadFile(space, path, file);
        return Result.OK().msg("success").data(null).build();

    }

    @PostMapping(value = "unZipFile")
    public Result unZipFile(String sourceSpace, String sourcePath, String fileName, String destSpace, String destPath)  {
        fileSpaceService.unZipFile(sourceSpace, sourcePath, fileName, destSpace,destPath);
        return Result.OK().msg("success").data(null).build();
    }

    @PostMapping(value = "deleteFile")
    public Result deleteFile(String space, String path, String fileName)  {
        fileSpaceService.deleteFile(space, path, fileName);
        return Result.OK().msg("success").data(null).build();
    }

    @PostMapping(value = "downFile")
    public void downFile(String space, String path, String fileName, HttpServletResponse response) throws IOException {
        fileSpaceService.downloadFile(space, path, fileName,response);
    }

    @PostMapping(value = "copyFile")
    public Result copyFile(String sourceSpace, String sourcePath, String sourceFileName,
                         String destSpace, String destPath, String destFileName) throws IOException {
        fileSpaceService.copyFile(sourceSpace, sourcePath, sourceFileName, destSpace, destPath, destFileName);
        return Result.OK().msg("success").data(null).build();
    }
}
