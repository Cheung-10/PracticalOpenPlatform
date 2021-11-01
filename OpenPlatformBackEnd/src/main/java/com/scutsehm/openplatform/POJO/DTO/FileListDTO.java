package com.scutsehm.openplatform.POJO.DTO;

import lombok.Data;

import java.util.List;

@Data
public class FileListDTO {
    private List<FileDTO> fileDTOList;
    private String path;


}
