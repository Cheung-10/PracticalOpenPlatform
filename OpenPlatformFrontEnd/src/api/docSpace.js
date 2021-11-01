import request from '@/utils/request'

export function getFiles(){
  return request({
    url:'/fileSpace/getFiles',
    method:'post',
    data,         //拿到文件空间对应路径的文件列表
  })
}

export function makeDir(){
  return request({
    url:'/fileSpace/makeDir',
    method:'post',
    data,        //创建新的文件夹
  })
}

export function uploadFile(){
  return request({
    url:'/fileSpace/uploadFile',
    method:'post',
    data,        //上传文件
  })
}

export function unZipFile(){
  return request({
    url:'/fileSpace/unZipFile',
    method:'post',
    data,        //解压zip压缩包
  })
}

export function deleteFile(){
  return request({
    url:'/fileSpace/deleteFile',
    method:'post',
    data,        //删除文件
  })
}

export function downFile(){
  return request({
    url:'/fileSpace/downFile',
    method:'post',
    data,        //下载文件
  })
}

export function copyFile(){
  return request({
    url:'/fileSpace/copyFile',
    method:'post',
    data,        //复制文件
  })
}