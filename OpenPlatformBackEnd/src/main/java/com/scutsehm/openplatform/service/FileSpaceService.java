package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.DTO.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileSpaceService {

    List<FileDTO> getFiles(String space,String path);

    boolean makeDir(String space,String path,String dirName);

    void uploadFile(String space, String path, MultipartFile file) throws IOException;

    void unZipFile(String sourceSpace, String sourcePath, String fileName, String destSpace, String destPath);

    void downloadFile(String space, String path, String fileName, HttpServletResponse response) throws IOException;

    void deleteFile(String space,String path,String fileName);

    void copyFile(String sourceSpace, String sourcePath, String sourceFileName,
                  String destSpace, String destPath, String destFileName);
}
