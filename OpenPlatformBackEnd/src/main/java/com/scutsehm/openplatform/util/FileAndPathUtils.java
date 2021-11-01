package com.scutsehm.openplatform.util;

import com.scutsehm.openplatform.POJO.entity.SpacePath;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.config.FilePathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

/**
 * 涉及到文件和路径的工具
 */
@Component
public class FileAndPathUtils {
    private static FilePathConfig filePathConfig;

    @Autowired
    public void init(FilePathConfig filePathConfig) {
        FileAndPathUtils.filePathConfig = filePathConfig;
    }

    /**
     * 获取随机名称
     * 原理自UUID
     * @return 随机名称
     */
    public static String getRandomName(){
        return UUID.randomUUID().toString();
    }

    /**
     * 根据传入空间来拼接路径
     * @param space 文件空间
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String space, String path){
        return getAbsolutePath(FileSpace.valueOf(space.toUpperCase()), path);
    }

    /**
     * 根据传入空间来拼接路径
     * @param space 文件空间
     * @param path 相对路径
     * @return 绝对路径
     */
    //FIXME 直接拼接路径，没有经过path.join等操作
    public static String getAbsolutePath(FileSpace space, String path){
        switch (space){
            case PRIVATE: return getAbsolutePathByPrivateSpace(path);
            case DATA: return getAbsolutePathByDataSpace(path);
            case SHARE: return getAbsolutePathByShareSpace(path);
            case PROCESSMODEL:return getAbsolutePathByProcessModelSpace(path);
            case TRAINMODEL: return getAbsolutePathByTrainModelSpace(path);
        }
        throw new RuntimeException("Invalid parameter space");
    }

    public static String getAbsolutePath(SpacePath spacePath){
        return getAbsolutePath(spacePath.getSpace(), spacePath.getPath());
    }
    /**
     * 由用户空间的相对路径拼接为绝对路径
     * @param privatePath 路径（用户空间下的）
     * @return 绝对路径
     */
    public static String getAbsolutePathByPrivateSpace(String privatePath){
        //直接拼接，JVM会优化
        return filePathConfig.getPrivateSpace() + "/" + UserUtil.getUsername() + privatePath;
    }

    /**
     * 由数据空间的相对路径拼接为绝对路径
     * @param dataPath 路径（数据空间下的）
     * @return 绝对路径
     */
    public static String getAbsolutePathByDataSpace(String dataPath){
        //直接拼接，JVM会优化
        return filePathConfig.getDataSpace() + dataPath;
    }

    /**
     * 由共享空间的相对路径拼接为绝对路径
     * @param sharePath 路径（共享空间下的）
     * @return 绝对路径
     */
    public static String getAbsolutePathByShareSpace(String sharePath){
        //直接拼接，JVM会优化
        return filePathConfig.getShareSpace() + sharePath;
    }

    /**
     * 由训练模型空间的相对路径拼接为绝对路径
     * @param trainModelPath 路径（训练模型空间下的）
     * @return 绝对路径
     */
    public static String getAbsolutePathByTrainModelSpace(String trainModelPath){
        //直接拼接，JVM会优化
        return filePathConfig.getTrainModelSpace() + trainModelPath;
    }

    /**
     * 由处理模型空间的相对路径拼接为绝对路径
     * @param processModelPath 路径（处理模型空间下的）
     * @return 绝对路径
     */
    public static String getAbsolutePathByProcessModelSpace(String processModelPath){
        //直接拼接，JVM会优化
        return filePathConfig.getProcessModelSpace() + processModelPath;
    }

    /**
     * 复制所有文件
     * @param from
     * @param to
     */
    public static void copy(SpacePath from, SpacePath to) throws IOException {
        copy(Paths.get(getAbsolutePath(from)), Paths.get(getAbsolutePath(to)));
    }

    /**
     * 复制整个文件夹
     * fromPath下的所有文件与文件夹都会被复制到toPath中
     * @param fromPath 被复制的文件夹的路径
     * @param toPath    目标文件夹的路径
     * @throws IOException
     */
    public static void copy(Path fromPath, Path toPath) throws IOException {
        //判断目录存在性
        File file = fromPath.toFile();
        if(!file.isDirectory()){
            throw new FileNotFoundException("源目录不存在");
        }
        File to = toPath.toFile();
        if(!to.isDirectory()){
            to.mkdirs();
        }
        //遍历目录
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                if(!fromPath.equals(path)){
                    String relativePath = getRelativePath(path);
                    File target = new File(toPath  + relativePath);
                    target.mkdirs();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String relativePath = getRelativePath(path);
                File target = new File(toPath  + relativePath);
                copy(path.toFile(), target);
                return FileVisitResult.CONTINUE;
            }

            private String getRelativePath(Path path){
                return path.toString().substring(fromPath.toString().length());
            }
        });
    }

    /**
     * 复制单个文件
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copy(File fromFile, File toFile) throws IOException {

        if(!fromFile.isFile()){
            throw new FileNotFoundException("源文件不存在");
        }
        //采用FileChannel方法
        doCopyByFileChannel(fromFile, toFile);
    }

    /**
     * 慢一点点占CPU也少一点点
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    private static void doCopyByFiles(File fromFile, File toFile) throws IOException {
        Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 快一点点占CPU也多一点点
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    private static void doCopyByFileChannel(File fromFile, File toFile) throws IOException {
        //FileChannel 方法
        //复制文件
        FileInputStream fromInputStream = null;
        FileOutputStream toInputStream = null;
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try{
            fromInputStream = new FileInputStream(fromFile);
            toInputStream = new FileOutputStream(toFile);
            //获得文件通道
            fromChannel = fromInputStream.getChannel();
            toChannel = toInputStream.getChannel();
            //复制操作
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        }catch (IOException e){
            //直接抛出
            throw e;
        }finally {
            //抑制所有关闭异常
            if(fromInputStream!=null){
                try {
                    fromInputStream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if(toInputStream!=null){
                try {
                    toInputStream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if(fromChannel!=null){
                try {
                    fromChannel.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if(toChannel!=null){
                try {
                    toChannel.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    //FIXME 更好的方法是检查字符串首部，避免/..和..开头两种情况
    public static void validate(SpacePath spacePath){
        String path = spacePath.getPath();
        if(path.contains("/..")){
            throw new RuntimeException("路径含有..，非法");
        }
    }
    public static void validate(String spacePath){

        if(spacePath.contains("/..")){
            throw new RuntimeException("路径含有..，非法");
        }
    }

}
