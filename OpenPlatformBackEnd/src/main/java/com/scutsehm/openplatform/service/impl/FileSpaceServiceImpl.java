package com.scutsehm.openplatform.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.scutsehm.openplatform.POJO.DTO.FileDTO;
import com.scutsehm.openplatform.POJO.enums.Operation;
import com.scutsehm.openplatform.exception.BusinessException;
import com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode;
import com.scutsehm.openplatform.util.*;
import com.scutsehm.openplatform.service.FileSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class FileSpaceServiceImpl implements FileSpaceService {

    //返回当前目录下的所有文件
    @Override
    public List<FileDTO> getFiles(String space,String path) {
        FileAndPathUtils.validate(path);
        checkAccess(Operation.GetFileList,space);
        String absolutePath=FileAndPathUtils.getAbsolutePath(space,path);

        File file=new File(absolutePath);
        //若文件夹不存在则创建一个
        if(!file.exists()){
            file.mkdir();
        }
        List<FileDTO> fileDTOList=new ArrayList<>();
        if(file.isDirectory()){
            //获取路径下所有的文件
            File[] files=file.listFiles();
            for(File subFile:files){
                FileDTO fileDTO=new FileDTO();
                fileDTO.setFileName(subFile.getName());
                fileDTO.setDirectory(subFile.isDirectory());
                fileDTOList.add(fileDTO);
            }
        }
        return fileDTOList;
    }

    //创建文件夹
    @Override
    public boolean makeDir(String space,String path,String dirName) {
        FileAndPathUtils.validate(path);
        checkAccess(Operation.MakeDir,space);
        String absolutePathPre=FileAndPathUtils.getAbsolutePath(space,path);
        String absolutePath=PathUtil.join(absolutePathPre,dirName);
        File file=new File(absolutePath);
        //若同名文件夹已不存在返回flase
        //若文件夹不存在则创建新的文件夹并返回true
        if(file.exists()){
            return false;
        }else{
            file.mkdir();
            return true;
        }
    }


    //上传文件，如果原文件存在则会覆盖源文件
    @Override
    public void uploadFile(String space, String path, MultipartFile file) throws IOException {
        FileAndPathUtils.validate(path);
        checkAccess(Operation.UpLoad,space);
        String absoluteDirPath=FileAndPathUtils.getAbsolutePath(space,path);
        String absolutePath=PathUtil.join(absoluteDirPath,file.getOriginalFilename());

        File destFile=new File(absolutePath);
        //若原文件存在，则无法上传
        if(destFile.exists()){
            throw new BusinessException(BusinessErrorCode.FILEEXISTED);
        }
        InputStream inputStream = file.getInputStream();
        OutputStream outputStream = new FileOutputStream(destFile);
        IOUtils.copy(inputStream,outputStream);

    }

    //解压缩到指定文件夹
    @Override
    public void unZipFile(String sourceSpace, String sourcePath, String fileName, String destSpace, String destPath) {
        //如果压缩文件为非zip文件，报错
        if(!fileName.endsWith("zip")&&!fileName.endsWith("ZIP")){
            throw new BusinessException(BusinessErrorCode.UNZIPERROR);
        }
        FileAndPathUtils.validate(sourcePath);
        FileAndPathUtils.validate(destPath);
        checkAccess(Operation.Read,sourceSpace);
        checkAccess(Operation.UnZip,destSpace);
        String absolutePathPre=FileAndPathUtils.getAbsolutePath(sourceSpace,sourcePath);
        String absolutePath=PathUtil.join(absolutePathPre,fileName);
        String absoluteOutFilePath=FileAndPathUtils.getAbsolutePath(destSpace,destPath);
        File zipFile=new File(absolutePath);
        File outFile=new File(absoluteOutFilePath);
        if(outFile.exists()){
            outFile.mkdir();
        }
        try{
            ZipUtil.unzip(zipFile,outFile, Charset.forName("GBK"));
        }catch (Exception e){
            ZipUtil.unzip(zipFile,outFile, Charset.forName("UTF-8"));
        }
    }

    @Override
    public void downloadFile(String space, String path, String fileName, HttpServletResponse response) throws IOException {
        FileAndPathUtils.validate(path);
        checkAccess(Operation.DownLoad,space);
        //获得要下载的文件的完整路径
        String absolutePathPre=FileAndPathUtils.getAbsolutePath(space,path);
        String absolutePath=PathUtil.join(absolutePathPre,fileName);
        File file=new File(absolutePath);
        File inputFile;
        String tempZipFilePath="";
        //如果文件存在，则开始下载
        if(file.exists()){
            //判断是否为文件夹，如果是文件夹则需要打包压缩成zip格式后再下载
            if(file.isDirectory()){
                //打包到当前目录，使用默认编码UTF-8
                inputFile= ZipUtil.zip(file);
                fileName=fileName+".zip";
                tempZipFilePath=PathUtil.join(absolutePathPre,fileName);
            }else{
                inputFile=file;
            }
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(inputFile);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            }
            catch (Exception e) {
               throw e;
            }
            finally {

                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(),e);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(),e);
                    }
                }
            }
        }else{
            //文件不存在
            throw new BusinessException(BusinessErrorCode.FILEUNEXISTED);
        }
        //如果压缩文件夹产生临时压缩包，要记得删除掉
        FileUtil.delete(tempZipFilePath);

    }

    //删除文件或文件夹
    @Override
    public void deleteFile(String space, String path, String fileName) {
        FileAndPathUtils.validate(path);
        checkAccess(Operation.Delete,space);
        String absoluteDirPath=FileAndPathUtils.getAbsolutePath(space,path);
        String absolutePath=PathUtil.join(absoluteDirPath,fileName);
        FileUtil.delete(absolutePath);

    }

    @Override
    public void copyFile(String sourceSpace, String sourcePath, String sourceFileName, String destSpace, String destPath, String destFileName) {
        FileAndPathUtils.validate(sourcePath);
        FileAndPathUtils.validate(destSpace);
        checkAccess(Operation.CopyFrom,sourceSpace);
        checkAccess(Operation.CopyTo,destSpace);
        String absoluteSourcePath= PathUtil.join(FileAndPathUtils.getAbsolutePath(sourceSpace,sourcePath),sourceFileName);
        String absoluteDestPath=PathUtil.join(FileAndPathUtils.getAbsolutePath(destSpace,destPath),destFileName);
        File sourceFile=new File(absoluteSourcePath);
        File destFile=new File(absoluteDestPath);
        if(!sourceFile.exists()){
            //源文件不存在
            throw new BusinessException(BusinessErrorCode.COPYSOURCEFILEUNEXISTED);
        }
        if(destFile.exists()){
            //目标文件已存在
            throw new BusinessException(BusinessErrorCode.COPYDESTFILEEXISTED);
        }
        FileUtil.copy(absoluteSourcePath,absoluteDestPath,false);

    }

    @Deprecated
    //对从前端拿到的路径做转换处理，转换成服务器上的绝对路径
    public String pathConverter(String space,String path){
        //对path进行初步处理，将路径分隔符替换为当前系统锁使用的
        path= PathUtil.replaceSeparator(path);
        //对path再一步处理，如果是user类型的，需要加上用户名作为前缀
        if(space.equals("user")){
            String username = UserUtil.getUsername();
            //判断path是否为空，避免添加多余的分隔符
            if(path == null || path.length() == 0 || path.trim().length() == 0){
                path=username;
            }else{
                path=PathUtil.join(username,path);
            }
        }
        //接上系统文件前缀并返回
        return FileAndPathUtils.getAbsolutePath(space, path);
    }

    //统一验证文件访问的权限
    private void checkAccess(Operation operation,String space){
        //如果是管理员，直接替换成管理员权限
        if(UserUtil.isAdmin()){
            operation=Operation.Admin;
        }
        
        boolean canAccess=FileAccessUtil.canAccess(operation,space);
        if(!canAccess){
            throw new BusinessException(BusinessErrorCode.NOACCESSTOFILESPACE);
        }
    }
}
